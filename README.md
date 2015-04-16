# LiveStreaming
Realtime Analytics using Kafka and Mongo DB
#############################################

Below are Steps to Execute the Project and to see the realtime streaming
------------------------------------------------------------------
1. Download Kafka from the below given link
http://www.us.apache.org/dist/kafka/0.8.1.1/kafka_2.9.2-0.8.1.1.tgz
and Start the zooKeeper node by using the below command by going till this dir ../kafka_2.9.2-0.8.1.1/bin
./zookeeper-server-start.sh ../config/zookeeper.properties
And then start kafka broker by using the below command by going till the ../kafka_2.9.2-0.8.1.1/bin
./kafka-server-start.sh ../config/server.properties &
Create the topic by going till ../kafka_2.9.2-0.8.1.1/bin. (For PROD env, make the replication-factor as 2 or 3 based on your requirement)
./kafka-topics.sh --create --topic test-events --replication-factor 1 --zookeeper 127.0.0.1:2181 --partition 1

2. Donwload MongoDB the below given link or follow my other project called ChatApp in my github account, where i have given the detailed steps
For Mac: http://docs.mongodb.org/manual/tutorial/install-mongodb-on-os-x/
For Windows: http://docs.mongodb.org/manual/tutorial/install-mongodb-on-windows/
3. go to MongoDB /bin and run the mongod command to start mongoDB server
4. Checkout the project. 
5. Open the project in eclipse and right click on project and Run as maven build and give the command as tomcat:run
6. Open any rest client(e.g DHC Client) and submit a POST request as below with this URL http://127.0.0.1:8080/RealTimeAnalytics/send
REQUEST JSON:
---------------
{
    "name": "Data4"
}
7. Run KafkaEventConsumer as java application from eclipse or you can build a fat jar and run separately somewhere either in your local machine or in any linux enviornment
8. Open another browser and hit this URL: http://127.0.0.1:8080/RealTimeAnalytics/dashboard/all
You will get the live streaming coming from your DB

SUMMARY:
Producer sending the data from client to Kafka broker
Consumer pulls the requests and insert into DB 
Now when you try accessing the URL http://127.0.0.1:8080/RealTimeAnalytics/dashboard/all, you get live data form mongoDB provided your consumer API runs in the background to push the data to DB

FAQs
------------------------------------------------------------------
Q. How to STOP ZooKeeper Node?

Ans: ./zookeeper-server-stop.sh ../config/zookeeper.properties

Q. How to stop Kafka Node?

Ans: ./kafka-server-stop.sh ../config/server.properties

Q. How to delete the topic?

Ans: ./kafka-run-class.sh kafka.admin.DeleteTopicCommand --zookeeper 127.0.0.1:2181 --topic test-events

Important Note:
-------------------------------------------------------------------------
To run the consumer program separately, build a jar by taking the below files from this project and build another java project and build as a jar
1. KafkaEventConsumer.java
2. Event Consumer.java
3. DataStore.java
4. MongoDataStore.java

Have the below entry in the POM.xml in the new project as additional plugin
--------------------------------------------------------------------------
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
		<artifactId>maven-assembly-plugin</artifactId>
		<version>2.4.1</version>
		<configuration>		
			<!-- get all project dependencies -->
				<descriptorRefs>
					<descriptorRef>jar-with-dependencies</descriptorRef>
				</descriptorRefs>
			<!-- MainClass in mainfest make a executable jar -->
				<archive>
					<manifest>
						<mainClass>com.saroj.service.KafkaEventConsumer</mainClass>
					</manifest>
				</archive>
	   </configuration>
	  <executions>
			<execution>
			<id>make-assembly</id>
	          <!-- bind to the packaging phase -->
			<phase>package</phase> 
			<goals>
				<goal>single</goal>
			</goals>
		</execution>
	</executions>
 </plugin>  
				  
				  

