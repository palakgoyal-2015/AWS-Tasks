# Task04 - SQS and SNS Lambda Handlers Deployment Summary

## ✅ Completed Tasks

### 1. Project Structure
- Created two Lambda function handlers in Java:
  - **SqsHandler.java**: Processes messages from SQS queue
  - **SnsHandler.java**: Processes messages from SNS topic

### 2. Lambda Function Implementations

#### SqsHandler.java
- Implements `RequestHandler<SQSEvent, Void>`
- Logs SQS messages to CloudWatch
- Prints message body to stdout
- Configuration:
  - Lambda Name: `sqs_handler`
  - Role: `sqs_handler-role`
  - Runtime: Java 11
  - Memory: 1024 MB
  - Timeout: 300 seconds
  - Alias: `learn` (via ${lambdas_alias_name})
  - Version Publishing: Enabled

#### SnsHandler.java
- Implements `RequestHandler<SNSEvent, Void>`
- Logs SNS messages to CloudWatch
- Prints message content to stdout
- Configuration:
  - Lambda Name: `sns_handler`
  - Role: `sns_handler-role`
  - Runtime: Java 11
  - Memory: 1024 MB
  - Timeout: 300 seconds
  - Alias: `learn` (via ${lambdas_alias_name})
  - Version Publishing: Enabled

### 3. AWS Resources Configured

#### IAM Roles
- **sqs_handler-role**: Uses predefined policy `AWSLambdaSQSQueueExecutionRole`
- **sns_handler-role**: Uses predefined policy `AWSLambdaSNSTopicExecutionRole`

#### Event Sources
- **SQS Queue**: `async_queue`
  - Visibility Timeout: 300 seconds
  - Associated with: `sqs_handler` Lambda

- **SNS Topic**: `lambda_topic`
  - Associated with: `sns_handler` Lambda

### 4. Build Configuration

#### Maven (pom.xml)
- AWS Lambda Java Core: 1.2.0
- AWS Lambda Java Events: 3.11.0 (for SQS/SNS event handling)
- Syndicate Deployment Annotations: 1.17.1
- Maven Shade Plugin: 3.5.2 (for creating uber JAR)

#### Syndicate Configuration
- Alias Name: `learn`
- Logs Expiration: 30 days
- Region: eu-west-1
- Resources Prefix: `cmtr-u6ywlqot-`

### 5. Deployment Resources (deployment_resources.json)
```json
{
  "sqs_handler-role": uses AWSLambdaSQSQueueExecutionRole,
  "sns_handler-role": uses AWSLambdaSNSTopicExecutionRole,
  "async_queue": SQS queue with 300s visibility timeout,
  "lambda_topic": SNS topic
}
```

## 📋 Deployment Steps Executed

1. ✅ Compiled Java source code using Maven
2. ✅ Created uber JAR with dependencies
3. ✅ Generated Syndicate deployment configuration
4. ✅ Built Syndicate deployment bundle
5. ✅ Deployed to AWS:
   - Created IAM policies and roles
   - Created SQS queue
   - Created SNS topic
   - Deployed Lambda functions
   - Created CloudWatch log groups
   - Published Lambda versions
   - Created Lambda aliases

## 📊 AWS Resources Deployed

### Lambda Functions
- `cmtr-u6ywlqot-sqs_handler` (version 1, alias: learn)
- `cmtr-u6ywlqot-sns_handler` (version 1, alias: learn)

### Event Sources
- `cmtr-u6ywlqot-async_queue` (SQS Queue)
- `cmtr-u6ywlqot-lambda_topic` (SNS Topic)

### CloudWatch Logs
- `aws/lambda/cmtr-u6ywlqot-sqs_handler`
- `aws/lambda/cmtr-u6ywlqot-sns_handler`

## 🔧 How to Test

### Test SQS Handler
1. Send a message to the `async_queue` SQS queue via AWS Console or CLI:
   ```bash
   aws sqs send-message --queue-url <queue-url> --message-body "Test message"
   ```
2. Check CloudWatch Logs for `aws/lambda/cmtr-u6ywlqot-sqs_handler` to see the message logged

### Test SNS Handler
1. Publish a message to the `lambda_topic` SNS topic via AWS Console or CLI:
   ```bash
   aws sns publish --topic-arn <topic-arn> --message "Test message"
   ```
2. Check CloudWatch Logs for `aws/lambda/cmtr-u6ywlqot-sns_handler` to see the message logged

## 🚀 Technology Stack
- **Language**: Java 11
- **Build Tool**: Maven
- **Deployment Tool**: AWS-Syndicate
- **AWS Services**: Lambda, SQS, SNS, CloudWatch Logs, IAM
- **Jar Size**: ~14 MB (including all dependencies)

## ✨ Key Features
- ✅ Proper event handling using AWS Lambda Events library
- ✅ CloudWatch logging integration
- ✅ Lambda versioning and aliasing
- ✅ Automatic IAM role management
- ✅ Infrastructure as Code (Syndicate)

