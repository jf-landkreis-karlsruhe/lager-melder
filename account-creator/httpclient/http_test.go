package httpclient

import (
	"encoding/json"
	"io"
	"net/http"
	"net/http/httptest"
	"testing"

	"github.com/kordondev/lager-melder/models"
)

func TestAddHeadDepartmentSetsShortNameAndHeadDepartment(t *testing.T) {
	var saved RestDepartment
	server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path != "/departments/1" {
			t.Fatalf("unexpected path %s", r.URL.Path)
		}
		switch r.Method {
		case http.MethodGet:
			json.NewEncoder(w).Encode(RestDepartment{
				Name:        "Auerbach",
				LeaderName:  "Auerbach",
				LeaderEMail: "vjf-auerbach@example.org",
			})
		case http.MethodPut:
			body, _ := io.ReadAll(r.Body)
			if err := json.Unmarshal(body, &saved); err != nil {
				t.Fatalf("invalid PUT body: %v", err)
			}
			w.Write(body)
		default:
			t.Fatalf("unexpected method %s", r.Method)
		}
	}))
	defer server.Close()

	department := models.Department{HeadDepartment: "Karlsbad", Name: "Auerbach", ShortName: "Auerbach"}
	AddHeadDepartment(models.RegisterResponse{DepartmentId: 1}, department, "token", server.URL)

	if saved.ShortName != "Auerbach" {
		t.Errorf("shortName = %q, want %q", saved.ShortName, "Auerbach")
	}
	if saved.HeadDepartmentName != "Karlsbad" {
		t.Errorf("headDepartmentName = %q, want %q", saved.HeadDepartmentName, "Karlsbad")
	}
}
