var http = require("http")
var request = require("request")
var express = require("express")
var redis = require("redis")
var util = require("util")
var app =  express()

var client = redis.createClient(6379, "127.0.0.1")

getRequest()

function getRequest(){
    client.on("connect", function(){
     setInterval(function(){
        client.rpop("Q:summer", function(err, reply){
            if(err){
                console.log(err)
            }else if(reply != null){
                console.log("popped data:" + reply)
                replyObj  = JSON.parse(reply)
                replyQueue = replyObj.replyQueue
                responseStr = processRequest(reply)
                client.lpush(replyQueue, responseStr, function(err,reply){
                    if(err){
                        console.log("Failed to send reply: " + err)
                    }else{
                        console.log("Successfully Inserted into Queue")
                    }
                })
            }else{
                console.log("Q:summer is empty")
            }
          })
     },1000)
    })

}

function processRequest(data){
    dataObj = JSON.parse(data);
    srcReplyQueue = dataObj.replyQueue;
    sum = Number(dataObj.params.numberOne) + Number(dataObj.params.numberTwo);
    responseStr = util.format(`{"timestamp":"%s","serviceName":"adder_msvc", "result":{"sum":%d},"replyQueue":"%s"}`,
                   new Date(), sum, srcReplyQueue);
    console.log("sum is: " + sum);
    return responseStr;
}