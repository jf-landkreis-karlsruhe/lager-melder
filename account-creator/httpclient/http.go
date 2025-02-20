package httpclient

import (
	"bytes"
	"encoding/json"
	"github.com/kordondev/lager-melder/models"
	"io"
	"log"
	"net/http"
	"strconv"
)

func CreateAccount(account models.AccountData, accessToken string, url string) models.RegisterResponse {
	// Create a new request using http
	req, err := http.NewRequest("POST", url+"/register", toBody(account))
	log.Println("Request ", account)

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

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		log.Println("Error while reading the response bytes:", err)
	}
	var registerResponse models.RegisterResponse
	json.Unmarshal(body, &registerResponse)
	log.Println("Created", registerResponse)
	return registerResponse
}

// kotlin to go

type RestDepartment struct {
	Name               string   `json:"name"`
	LeaderName         string   `json:"leaderName"`
	PhoneNumber        string   `json:"phoneNumber"`
	Features           []string `json:"features"`
	Paused             bool     `json:"paused"`
	LeaderEMail        string   `json:"leaderEMail"`
	ShortName          string   `json:"shortName"`
	HeadDepartmentName string   `json:"headDepartmentName"`
}

func AddHeadDepartment(department models.RegisterResponse, departments map[string]models.Department, accessToken, url string) {
	restDepartment := loadDepartment(department.DepartmentId, accessToken, url)
	myDepartment := departments[restDepartment.LeaderEMail]
	restDepartment.HeadDepartmentName = myDepartment.HeadDepartment
	restDepartment.ShortName = myDepartment.ShortName

	updateDepartment(department.DepartmentId, restDepartment, accessToken, url)
}

func loadDepartment(id int, accessToken, url string) RestDepartment {
	req, err := http.NewRequest("GET", url+"/departments/"+strconv.Itoa(id), nil)
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

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		log.Println("Error while reading the response bytes:", err)
	}
	var restDepartment RestDepartment
	json.Unmarshal(body, &restDepartment)
	return restDepartment
}

func updateDepartment(id int, department RestDepartment, accessToken, url string) RestDepartment {
	req, err := http.NewRequest("PUT", url+"/departments/"+strconv.Itoa(id), toBodyDepartment(department))
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

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		log.Println("Error while reading the response bytes:", err)
	}
	var restDepartment RestDepartment
	json.Unmarshal(body, &restDepartment)
	return restDepartment
}

func toBody(acc models.AccountData) *bytes.Reader {
	postBody, err := json.Marshal(acc)
	if err != nil {
		log.Fatalf("error by generation json %v", acc)
	}
	return bytes.NewReader(postBody)
}

func toBodyDepartment(dep RestDepartment) *bytes.Reader {
	postBody, err := json.Marshal(dep)
	if err != nil {
		log.Fatalf("error by generation json %v", dep)
	}
	return bytes.NewReader(postBody)
}
