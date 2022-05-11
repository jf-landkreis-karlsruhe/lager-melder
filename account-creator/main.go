package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"github.com/jszwec/csvutil"
	"io/ioutil"
	"log"
	"net/http"
	"os"
	"strings"
)

type AccountData struct {
	Username       string `json:"username"`
	DepartmentName string `json:"departmentName"`
	LeaderName     string `json:"leaderName"`
	LeaderEMail    string `json:"leaderEMail"`
}

type Login struct {
	Username string `json:"username"`
	Password string `json:"password"`
}

type DepartmentCSV struct {
	Name            string `csv:"Jugendfeuerwehr"`
	DepartmentName  string `csv:"Abteilung"`
	LeaderFirstName string `csv:"Vorname"`
	LeaderLastName  string `csv:"Nachname"`
	LeaderEMail     string `csv:"Emailadresse"`
}

func main() {
	departments := readCSV("./jugendwarte.csv")
	accounts := departmentToAccount(departments)

	token := login("admin", "")
	log.Println(token)
	for _, account := range accounts {
		if account.LeaderName == "STOPArne Jan Maier" {
			//TODO: fix max length of username
			account.Username = "Ka-La"
			createAccount(account, token)
		}
	}
}

func toBody(acc AccountData) *bytes.Reader {
	postBody, err := json.Marshal(acc)
	if err != nil {
		log.Fatalf("error by generation json %v", acc)
	}
	return bytes.NewReader(postBody)
}

func login(username string, password string) string {
	loginData := Login{
		username,
		password,
	}
	postBody, err := json.Marshal(loginData)
	if err != nil {
		log.Fatalf("error by generation json %v", loginData)
	}
	resp, err := http.Post("http://lager-melder.kordondev.de/api/login", "application/json", bytes.NewReader(postBody))
	if err != nil {
		log.Println("Error on response.\n[ERROR] -", err)
	}
	defer resp.Body.Close()

	body, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		log.Println("Error while reading the response bytes:", err)
	}
	var loginResponse LoginResponse
	json.Unmarshal(body, &loginResponse)
	return loginResponse.Authorization
}

func createAccount(account AccountData, accessToken string) {
	// https://stackoverflow.com/questions/51452148/how-can-i-make-a-request-with-a-bearer-token-in-go
	// url := "http://localhost:8080/register"
	url := "https://lager-melder.kordondev.de/api/register"

	// Create a new request using http
	req, err := http.NewRequest("POST", url, toBody(account))

	// add authorization header to the req
	req.Header.Add("Authorization", token)
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

type RegisterResponse struct {
	UserId         string `json:"userId"`
	Username       string `json:"username"`
	Role           string `json:"role"`
	DepartmentId   int    `json:"departmentId"`
	DepartmentName string `json:"departmentName"`
	LeaderName     string `json:"leaderName"`
	LeaderEMail    string `json:"leaderEMail"`
}

type LoginResponse struct {
	Authorization string `json:"Authorization"`
}
