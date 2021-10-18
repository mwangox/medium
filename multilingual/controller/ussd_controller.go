package controller

import (
	"fmt"
	"log"
	"multilingual/service"
	"net/http"
)

func UssdHandler(w http.ResponseWriter, r *http.Request) {
	ussdCode := r.FormValue("ussdCode")
	language := r.FormValue("language")
	msisdn   := r.FormValue("msisdn")
	log.Printf("Ussd request received: %v, %v, %v", ussdCode, language, msisdn )

	messageConfig := service.NewMessageConfig()
	fmt.Fprintf(w, messageConfig["messages_"+language].GetString("messages.welcome"))
}
