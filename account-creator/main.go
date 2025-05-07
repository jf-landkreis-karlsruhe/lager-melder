package main

import (
	"github.com/kordondev/lager-melder/httpclient"
	"github.com/kordondev/lager-melder/models"
	"log"
	"os"
	"strings"
)

func main() {
	args := parseCommandLineArgs(os.Args[1:])

	accounts := departmentToAccount(models.Departments, args.Domain)

	for _, account := range accounts {
		response := httpclient.CreateAccount(account, args.Token, args.Url)
		httpclient.AddHeadDepartment(response, models.Departments, args.Token, args.Url)
	}
}

func departmentToAccount(departments map[string]models.Department, domain string) []models.AccountData {
	var accounts []models.AccountData
	for email, department := range departments {
		account := models.AccountData{
			Username:       email + domain,
			DepartmentName: department.Name,
			LeaderName:     department.Name,
			LeaderEMail:    email + domain,
			Features:       []string{"YOUTH_GROUPS", "CHILD_GROUPS"},
			//Features: []string{"YOUTH_GROUPS", "HELPER"},
			//Features: []string{"HELPER", "ZKIDS"},
		}
		account.Username = strings.ReplaceAll(account.Username, " ", "")
		accounts = append(accounts, account)
	}
	return accounts
}

func parseCommandLineArgs(args []string) CommandLineArgs {
	parsedArgs := CommandLineArgs{}
	for _, arg := range args {
		splitArg := strings.Split(arg, "=")
		if splitArg[0] == "-t" {
			parsedArgs.Token = splitArg[1]
		}
		if splitArg[0] == "-d" {
			parsedArgs.Domain = splitArg[1]
		}
		if len(splitArg) == 1 {
			parsedArgs.Url = splitArg[0]
		}
	}

	if parsedArgs.Token == "" || parsedArgs.Url == "" {
		log.Fatal("you need to provide '-t=' for the login token, '-d=' for the email domain")
	}
	return parsedArgs
}

type CommandLineArgs struct {
	Token  string
	Url    string
	Domain string
}
