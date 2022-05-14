package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
	"os"
	"strings"

	"github.com/jszwec/csvutil"
)

type AccountData struct {
	Username       string `json:"username"`
	DepartmentName string `json:"departmentName"`
	LeaderName     string `json:"leaderName"`
	LeaderEMail    string `json:"leaderEMail"`
}

type DepartmentCSV struct {
	Name            string `csv:"Jugendfeuerwehr"`
	DepartmentName  string `csv:"Abteilung"`
	LeaderFirstName string `csv:"Vorname"`
	LeaderLastName  string `csv:"Nachname"`
	LeaderEMail     string `csv:"Emailadresse"`
}

func main() {
	args := parseCommandLineArgs(os.Args[1:])

	departments := readCSV(args.CsvPath)
	accounts := departmentToAccount(departments)

	for _, account := range accounts {
		createAccount(account, args.Token, args.Url)
	}
}

func toBody(acc AccountData) *bytes.Reader {
	postBody, err := json.Marshal(acc)
	if err != nil {
		log.Fatalf("error by generation json %v", acc)
	}
	return bytes.NewReader(postBody)
}

func createAccount(account AccountData, accessToken string, url string) {
	// Create a new request using http
	req, err := http.NewRequest("POST", url, toBody(account))

	// add authorization header to the req
	req.Header.Add("Authorization", "Bearer "+accessToken)
	req.Header.Add("Content-Type", "application/json")

	// Send req using http Client
	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		log.Println("Error on response.\n[ERROR] -", err)
	}
	if resp.StatusCode != 200 && resp.StatusCode != 201 {
		log.Println("Error on request", resp.Status)
	}
	defer resp.Body.Close()

	body, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		log.Println("Error while reading the response bytes:", err)
	}
	var registerResponse RegisterResponse
	json.Unmarshal(body, &registerResponse)
	log.Println("Created", registerResponse)
}

func readCSV(csvInputFile string) []DepartmentCSV {
	data, err := os.ReadFile(csvInputFile)
	if err != nil {
		log.Fatalf("error", err)
	}
	var department []DepartmentCSV
	if err := csvutil.Unmarshal(data, &department); err != nil {
		fmt.Println("error:", err)
	}
	return department
}

func departmentToAccount(departments []DepartmentCSV) []AccountData {
	var accounts []AccountData
	for _, department := range departments {
		account := AccountData{
			Username:       accountName(department),
			DepartmentName: department.Name + " " + department.DepartmentName,
			LeaderName:     department.LeaderFirstName + " " + department.LeaderLastName,
			LeaderEMail:    department.LeaderEMail,
		}
		account.Username = strings.ReplaceAll(account.Username, " ", "")
		accounts = append(accounts, account)
	}
	return accounts
}

func accountName(department DepartmentCSV) string {
	if department.DepartmentName == "" {
		return strings.ReplaceAll(department.Name, " ", "")
	}
	return strings.ReplaceAll(department.Name+"-"+department.DepartmentName, " ", "")
}

func parseCommandLineArgs(args []string) CommandLineArgs {
	parsedArgs := CommandLineArgs{}
	for _, arg := range args {
		splitArg := strings.Split(arg, "=")
		if splitArg[0] == "-t" {
			parsedArgs.Token = splitArg[1]
		}
		if splitArg[0] == "-u" {
			parsedArgs.CsvPath = splitArg[1]
		}
		if len(splitArg) == 1 {
			parsedArgs.Url = splitArg[0]
		}
	}

	fmt.Println(parsedArgs)
	if parsedArgs.CsvPath == "" || parsedArgs.Token == "" || parsedArgs.Url == "" {
		log.Fatal("you need to provide '-t=' for the login token, '-u=' for users as csv and the url")
		os.Exit(1)
	}
	return parsedArgs
}

type CommandLineArgs struct {
	Token   string
	CsvPath string
	Url     string
}

type RegisterResponse struct {
	UserId         string `json:"userId"`
	Username       string `json:"username"`
	Role           string `json:"role"`
	DepartmentId   int    `json:"departmentId"`
	DepartmentName string `json:"departmentName"`
	LeaderName     string `json:"leaderName"`
	LeaderEMail    string `json:"leaderEMail"`
}
