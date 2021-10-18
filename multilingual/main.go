package main

import (
	"log"
	"multilingual/controller"
	"multilingual/service"
	"net/http"
)

func main()  {
	log.Println("Initialize message configurations...")
	service.LoadMessagesToMap()

	http.HandleFunc("/ussd", controller.UssdHandler)
	http.ListenAndServe(":12000", nil)
}


