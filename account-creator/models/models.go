package models

type AccountData struct {
	Username       string   `json:"username"`
	DepartmentName string   `json:"departmentName"`
	LeaderName     string   `json:"leaderName"`
	LeaderEMail    string   `json:"leaderEMail"`
	Features       []string `json:"features"`
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
type Department struct {
	HeadDepartment string
	Name           string
	ShortName      string
}
