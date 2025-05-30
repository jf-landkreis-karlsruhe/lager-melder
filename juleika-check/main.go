package main

import (
	"bufio"
	"encoding/json"
	"fmt"
	"io"
	"log"
	"net/http"
	"os"
	"strings"
	"time"
)

func padJuleikaNumber(number string) string {
	number = strings.TrimSpace(number)
  if (strings.HasPrefix(number, "0")) {
    return number
  }

	return "0" + number
}

func trimAll(s string) string {
	return strings.TrimSpace(s)
}

func main() {
	juleikaFileName := os.Args[1]
	apiToken := "" // TODO: Move
	eventDate, _ := time.Parse("02.01.2006", "18.06.2025")

	file, err := os.Open(juleikaFileName)
	if err != nil {
		log.Fatalf("Datei konnte nicht geöffnet werden: %v", err)
	}
	defer file.Close()

	var validList []*ValidatedJuleika
	var invalidLastnameList []*ValidatedJuleika
	var invalidExpireDateList []*ValidatedJuleika
	var invalidList []*ValidatedJuleika
	var earlyInvalidationList []*ValidatedJuleika
	var errorList []JuleikaCheck

	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		line := scanner.Text()
		fields := strings.Split(line, "|")
		if len(fields) != 5 {
			fmt.Println("Ungültige Zeile:", line)
			continue
		}

		lastName := trimAll(fields[1])
		number := padJuleikaNumber(fields[2])
		date, err := time.Parse("2006-01-02", trimAll(fields[3])) // 2027-01-31

		// Prüfen, ob das Datum korrekt ist
		if err != nil {
			fmt.Printf("Ungültiges Datum (%s): %s\n", number, date)
			continue
		}

		check := JuleikaCheck{
			LastName: lastName,
			Number:   number,
			Date:     date,
		}

		juleikaResult, err := CheckJuleika(check, apiToken, eventDate)
		if err != nil {
			errorList = append(errorList, check)
			fmt.Printf("Fehler bei API-Abfrage: %v\n", err)
			continue
		}

    if !juleikaResult.IsValid {
      invalidList = append(invalidList, juleikaResult)
      continue
    }

		if juleikaResult.ValidTill.Before(eventDate) {
			earlyInvalidationList = append(earlyInvalidationList, juleikaResult)
			continue
		}

		if !juleikaResult.ExpirationDate.Equal(*juleikaResult.ValidTill) {
			invalidExpireDateList = append(invalidExpireDateList, juleikaResult)
			continue
		}

		if juleikaResult.IsValid && juleikaResult.CheckedLastname == "" {
			invalidLastnameList = append(invalidLastnameList, juleikaResult)
			continue
		}

    if juleikaResult.IsValid {
      validList = append(validList, juleikaResult)
      continue
    }

    fmt.Printf("Unbehandelter Fall %v", juleikaResult)
	}


	fmt.Printf("\n== GÜLTIGE JULEIKAS == (%d)\n", len(validList))
	for _, v := range validList {
		fmt.Printf("%s | %s | %s\n", v.CheckedLastname, v.JuleikaNumber, v.ValidTill.Format("02.01.2006"))
	}

	fmt.Printf("\n== UNGÜLTIGE Nachnamen == (%d)\n", len(invalidLastnameList))
	for _, v := range invalidLastnameList {
		fmt.Printf("%s | %s\n", v.JuleikaNumber, v.ValidTill.Format("02.01.2006"))
	}

	fmt.Printf("\n== UNGÜLTIGE EXPIRE DATE == (%d)\n", len(invalidExpireDateList))
	for _, v := range invalidExpireDateList {
		fmt.Printf("%s | %s | %s vs %s\n", v.CheckedLastname, v.JuleikaNumber, v.ValidTill.Format("02.01.2006"), v.ExpirationDate.Format("02.01.2006"))
	}

	fmt.Printf("\n== EXPIRE DATE ZU FRÜH == (%d)\n", len(earlyInvalidationList))
	for _, v := range earlyInvalidationList {
		fmt.Printf("%s | %s | %s\n", v.CheckedLastname, v.JuleikaNumber, v.ValidTill.Format("02.01.2006"))
	}

	fmt.Printf("\n== UNGÜLTIGE JULEIKAS == (%d)\n", len(invalidList))
	for _, v := range invalidList {
		fmt.Printf("%s\n", v.JuleikaNumber)
	}

	fmt.Printf("\n== ERROR JULEIKAS == (%d)\n", len(errorList))
	for _, v := range errorList {
		fmt.Printf("%s | %s | %s\n", v.LastName, v.Number, v.Date.Format("02.01.2006"))
	}
}

type JuleikaCheck struct {
	LastName string
	Number   string
	Date     time.Time
	Valid    bool
}

type ApiResponse struct {
	Status    string `json:"status"`
	ValidTill string `json:"valid_till"`
}

type ValidatedJuleika struct {
	IsValid         bool
	ValidTill       *time.Time
	CheckedLastname string
	JuleikaNumber   string
	ExpirationDate  time.Time
}

func CheckJuleika(juleika JuleikaCheck, token string, eventDate time.Time) (*ValidatedJuleika, error) {
	fullCheck, err := CheckJuleikaAPI(juleika, token)
	if err != nil {
		return nil, err
	}

	if fullCheck == nil || !fullCheck.IsValid {
		juleika.LastName = ""
		secondCheck, err := CheckJuleikaAPI(juleika, token)
		if err != nil {
			return nil, err
		}
		return secondCheck, nil
	}

  time.Sleep(10 * time.Millisecond)
	return fullCheck, nil
}

func CheckJuleikaAPI(juleika JuleikaCheck, token string) (*ValidatedJuleika, error) {
	url := fmt.Sprintf("https://app.juleica-antrag.de/api/card-is-valid/?card_number=%s&lastname=%s", juleika.Number, juleika.LastName)
	fmt.Println(url)

	// make HTTP GET request with auth token
	request, err := http.NewRequest("GET", url, nil)
	request.Header.Set("Authorization", fmt.Sprintf("Bearer %s", token))
	request.Header.Set("Accept", "application/json")

	client := &http.Client{}

	resp, err := client.Do(request)
	if err != nil {
		return nil, err
	}

	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("HTTP Fehler: %s", resp.Status)
	}

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return nil, err
	}

	var result ApiResponse
	if err := json.Unmarshal(body, &result); err != nil {
		return nil, err
	}

	if result.Status != "valid" {
		return &ValidatedJuleika{
			IsValid:         false,
			ValidTill:       nil,
			CheckedLastname: juleika.LastName,
			JuleikaNumber:   juleika.Number,
			ExpirationDate:  juleika.Date,
		}, nil

	}

	validTill, err := time.Parse("02.01.2006", result.ValidTill) // 31.01.2027
	if err != nil {
    fmt.Println("Falsches Datum ")
		return nil, fmt.Errorf("Ungültiges Datum im API-Antwort: %s", result.ValidTill)
	}

	return &ValidatedJuleika{
		IsValid:         result.Status == "valid",
		ValidTill:       &validTill,
		CheckedLastname: juleika.LastName,
		JuleikaNumber:   juleika.Number,
		ExpirationDate:  juleika.Date,
	}, nil

}
