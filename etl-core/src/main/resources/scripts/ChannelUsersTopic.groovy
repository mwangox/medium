package scripts

import groovy.json.JsonSlurper
import groovy.sql.Sql
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import groovyx.net.http.RESTClient

try {
    requestMap = new JsonSlurper().parseText(request.toString())
    log.info("Converted request into map: ${requestMap}")

    try {
        def mysqlUrl = "jdbc:mysql://localhost:3306/channel_reports"
        def userName = "root"
        def password = "root"
        def sql = Sql.newInstance(mysqlUrl, userName, password)
        sql.executeInsert("""insert into channel_users values(${requestMap.customer_id}, 
                                 ${requestMap.event_source_id},
                                 ${requestMap.event_time},
                                 ${requestMap.event_type}, 
                                 ${requestMap.customer_name})""")
        sql.updateCount > 0 ? log.info("Data saved successfully into mysql: ${sql.updateCount}") : log.error("Failed to save data into mysql database: ${sql.updateCount}")
    }catch(Exception e){
        log.error("Failed to save data into mysql database: ${e.getMessage()}")
    }

    try{
        def elasticUrl = "http://localhost:9200/channel-users/_doc"
        def client = new RESTClient(elasticUrl)

        client.request(Method.POST) {
            requestContentType = ContentType.JSON
            body = requestMap
            response.success = { resp -> log.info("Data saved successfully into elasticSearch: ${resp.status}")}
            response.failure = { resp -> log.info("Failed to save data to elasticSearch: ${resp.status}")}
        }
    }catch(Exception e){
        log.error("Failed to save data into elasticSearch due to: ${e.getMessage()}")
    }
}catch(Exception e){
    log.info("Failed to process the request: {}", e.getMessage())
}



