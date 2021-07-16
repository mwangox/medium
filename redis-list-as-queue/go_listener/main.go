package main

import (
	"fmt"
	"log"
	"net/http"
	"time"

	"github.com/garyburd/redigo/redis"
	"github.com/magiconair/properties"

	"github.com/google/uuid"
)

var pool redis.Pool
var config = properties.MustLoadFile("./conf/application.properties", properties.UTF8)

func init() {

	pool = redis.Pool{
		MaxActive:   config.MustGetInt("redis.pool.maxActive"),
		MaxIdle:     config.MustGetInt("redis.pool.maxIdle"),
		IdleTimeout: 60 * time.Second,
		Wait:        true,
		Dial: func() (redis.Conn, error) {
			c, err := redis.Dial("tcp", fmt.Sprintf("%s:%s", config.MustGetString("redis.host"), config.MustGetString("redis.port")))
			if err != nil {
				return nil, err
			}

			return c, nil
		},
		TestOnBorrow: func(conn redis.Conn, t time.Time) error {
			_, err := conn.Do("PING")

			return err

		},
	}

}

func sum(w http.ResponseWriter, r *http.Request) {
	log.Println("Received request: ", r.URL.Query())
	params := r.URL.Query()

	guuid := uuid.New().String()
	replyAddress := fmt.Sprintf("QR:http:%s", guuid)
	timestamp := time.Now().Format("2006-01-02 15:04:05.000")
	numberOne := params.Get("numberOne")
	numberTwo := params.Get("numberTwo")
	timeToLive := 20
	//Query String for msvc
	requestStr := fmt.Sprintf(`
	{
		"timestamp":"%s",
		"serviceName":"adder_msvc",
		"ttl":"%d",
		"params":{"numberOne":"%s", "numberTwo":"%s"},
		"replyQueue":"%s"
	}`, timestamp, timeToLive, numberOne, numberTwo, replyAddress)

	//  Send Request to apigw
	conn := pool.Get()
	defer conn.Close()
	_, err := conn.Do("lpush", "Q:summer", requestStr)
	if err != nil {
		fmt.Println("Error during writing into summer queue", err)
	}
	//Blocking right pop command with timeout
	result, err := redis.StringMap(conn.Do("brpop", replyAddress, timeToLive))
	if err != nil {
		fmt.Fprintln(w, "Error during reading reply Queue", err)
	}

	for _, v := range result {
		fmt.Fprintln(w, v)
	}

}
func main() {

	http.HandleFunc("/sum", sum)
	log.Println("Server is listening at: ", 9090)
	http.ListenAndServe(":9090", nil)
}
