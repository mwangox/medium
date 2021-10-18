package service

import (

	"github.com/fsnotify/fsnotify"
	"github.com/spf13/viper"
	"log"
)

var messageConfig map[string]*viper.Viper

func LoadMessagesToMap(){
	files := []string{"messages_en", "messages_sw"}
	mapConfig := make(map[string]*viper.Viper)
	for _, file := range files {
		viper := viper.New()
		viper.SetConfigName(file)
		viper.AddConfigPath("./conf/")
		viper.WatchConfig()
		viper.OnConfigChange(func(e fsnotify.Event) {
			log.Println("Message file  updated:", e.Name)
		})

		if err := viper.ReadInConfig(); err != nil {
			log.Fatalf("Failed to read %v config file: %v", file, err)
		}
		mapConfig[file] = viper
	}
	messageConfig = mapConfig
	log.Println("Messages loaded successfully into map")
}

func NewMessageConfig()map[string]*viper.Viper {
	return messageConfig
}
