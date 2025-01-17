import pika
import json
import sys
import csv
from datetime import datetime, timedelta
import time

# RabbitMQ connection parameters
rabbitmq_host = 'localhost'
rabbitmq_user = 'admin'
rabbitmq_password = 'admin'
queue_name = 'monitoring'
delay = 1

# Parse command-line arguments
if len(sys.argv) > 1:
    try:
        delay = int(sys.argv[1])
    except ValueError:
        print("Invalid number provided. Using default delay.")
if len(sys.argv) > 2:
    try:
        number = int(sys.argv[2])
    except ValueError:
        print("Invalid number provided. Using default number.")
        number = 1
else:
    number = 1

ids = sys.argv[3:] if len(sys.argv) > 3 else ["51eacd1d-6abb-4f05-9f02-c3d5e93f3291"]

# Connect to RabbitMQ server
credentials = pika.PlainCredentials(rabbitmq_user, rabbitmq_password)
connection = pika.BlockingConnection(pika.ConnectionParameters(host=rabbitmq_host, credentials=credentials))
channel = connection.channel()

# Declare the queue
channel.queue_declare(queue=queue_name, durable=True)

# Initialize the starting timestamp
current_time = int(datetime.now().timestamp() * 1000)

with open("sensor.csv", mode='r') as csvfile:
    csv_reader = csv.reader(csvfile)
    lastRead = 0
    currentRead = 0
    for row in csv_reader:
        try:
            currentRead = float(row[0])
        except ValueError:
            print(f"Invalid measurement value in row: {row}")
            continue

        now = current_time  # Use the current timestamp
        
        measurement_value = currentRead - lastRead
        lastRead = currentRead

        for device_id in ids:
            message = {
                "deviceId": device_id,
                "measurementValue": measurement_value,
                "timestamp": now
            }

            channel.basic_publish(
                exchange='',
                routing_key=queue_name,
                body=json.dumps(message),
                properties=pika.BasicProperties(
                    delivery_mode=2,  # Make message persistent
                )
            )

            print(f"Message sent: {message}")

        time.sleep(delay)  # Delay of 1 second between processing rows
        current_time += 10 * 60 * 1000  # Increment timestamp by 10 minutes

# Close the connection
connection.close()
