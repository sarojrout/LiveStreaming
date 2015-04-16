# LiveStreaming
Realtime Analytics using Kafka and MongoDB
Steps to Execute the Project and to see the realtime streaming
1. Download Kafka from the below given link
http://www.us.apache.org/dist/kafka/0.8.1.1/kafka_2.9.2-0.8.1.1.tgz
and Start the zooKeeper node by using the below command by going till ../kafka_2.9.2-0.8.1.1/bin
./zookeeper-server-start.sh ../config/zookeeper.properties
And then start kafka broker by using the below command by going till the ../kafka_2.9.2-0.8.1.1/bin
./kafka-server-start.sh ../config/server.properties &
Create the topic by going till ../kafka_2.9.2-0.8.1.1/bin. (For PROD env, make the replication-factor as 2 or 3 based on your requirement)
./kafka-topics.sh --create --topic test-events --replication-factor 1 --zookeeper 127.0.0.1:2181 --partition 1

2. Donwload MongoDB the below given link or follow my other project called ChatApp in my github account, where i have given the detailed steps
For Mac: http://docs.mongodb.org/manual/tutorial/install-mongodb-on-os-x/
For Windows: http://docs.mongodb.org/manual/tutorial/install-mongodb-on-windows/
3. Checkout the project
4. go to MongoDB /bin and run the mongod command to start mongoDB server
5. Run tomcat as mvn tomcat:run
6. Open the any rest client and submit a POST request as below with this URL http://127.0.0.1:8080/RealTimeAnalytics/send
{
    "name": "Data4"
}
7. Run KafkaEventConsumer as java application from eclipse or you can build a fat jar and run separately somewhere either in your local machine or in any linux envornment
8. Then Open another browser and hit this URL: http://127.0.0.1:8080/RealTimeAnalytics/dashboard/all
You will get the live streaming coming from your DB

SUMMARY:
Producer sending the data from client to Kafka broker
Consumer pulls the requests and insert into DB 
Now when you try accessing the URL for live stream data using this URL http://127.0.0.1:8080/RealTimeAnalytics/dashboard/all, you get the response from mongo DB


