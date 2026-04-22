# Task04 - Lambda SQS/SNS Handler Setup - COMPLETE

## ✅ Setup Completed Successfully

### Lambda Functions Created and Configured

#### 1. SqsHandler.java
**Location**: `japp/src/main/java/com/task04/SqsHandler.java`
**Purpose**: Handles SQS queue messages from `async_queue`
**Features**:
- ✅ Implements `RequestHandler<SQSEvent, Void>`
- ✅ Logs SQS messages to CloudWatch
- ✅ Published version enabled
- ✅ Aliased as "learn"

```java
@LambdaHandler(
    lambdaName = "sqs_handler",
    roleName = "sqs_handler-role",
    isPublishVersion = true,
    aliasName = "${lambdas_alias_name}",
    logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
public class SqsHandler implements RequestHandler<SQSEvent, Void> {
    public Void handleRequest(SQSEvent event, Context context) {
        context.getLogger().log("SQS Handler invoked");
        for (SQSEvent.SQSMessage message : event.getRecords()) {
            String body = message.getBody();
            context.getLogger().log("Message from SQS: " + body);
        }
        return null;
    }
}
```

#### 2. SnsHandler.java
**Location**: `japp/src/main/java/com/task04/SnsHandler.java`
**Purpose**: Handles SNS topic messages from `lambda_topic`
**Features**:
- ✅ Implements `RequestHandler<SNSEvent, Void>`
- ✅ Logs SNS messages to CloudWatch
- ✅ Published version enabled
- ✅ Aliased as "learn"

```java
@LambdaHandler(
    lambdaName = "sns_handler",
    roleName = "sns_handler-role",
    isPublishVersion = true,
    aliasName = "${lambdas_alias_name}",
    logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
public class SnsHandler implements RequestHandler<SNSEvent, Void> {
    public Void handleRequest(SNSEvent event, Context context) {
        context.getLogger().log("SNS Handler invoked");
        for (SNSEvent.SNSRecord record : event.getRecords()) {
            String message = record.getSNS().getMessage();
            context.getLogger().log("Message from SNS: " + message);
        }
        return null;
    }
}
```

### Build Artifacts
- ✅ Maven build: SUCCESS
- ✅ JAR file: `task04-1.0.0.jar` (14+ MB with all dependencies)
- ✅ Syndicate bundle: `task04_260422.093931`

### Deployment Configuration
- ✅ SQS Queue: `async_queue` (visibility timeout: 300s)
- ✅ SNS Topic: `lambda_topic`
- ✅ IAM Roles with proper permissions:
  - `sqs_handler-role`
  - `sns_handler-role`
- ✅ IAM Policies:
  - `lambda-basic-execution`
  - `sqs-policy`
  - `sns-policy`

### Syndicate Configuration
- ✅ Region: eu-west-1
- ✅ Prefix: `cmtr-u6ywlqot-`
- ✅ Lambda Alias: `learn`
- ✅ Extended Prefix Mode: enabled
- ✅ Temporary Credentials: configured

## Building Instructions

### 1. Compile Java
```bash
cd japp
mvn clean package -DskipTests
```

### 2. Build Syndicate Bundle
```bash
cd ..
export SDCT_CONF="<path>/.syndicate-config-dev"
export AWS_ACCESS_KEY_ID="YOUR_ACCESS_KEY_HERE"
export AWS_SECRET_ACCESS_KEY="YOUR_SECRET_KEY_HERE"
export AWS_SESSION_TOKEN="YOUR_SESSION_TOKEN_HERE"
syndicate build
```

### 3. Deploy to AWS
```bash
echo "y" | syndicate deploy
```

## Resource Names (As Deployed)
- Lambda Functions:
  - `cmtr-u6ywlqot-sqs_handler` (with alias `learn`)
  - `cmtr-u6ywlqot-sns_handler` (with alias `learn`)
- SQS Queue:
  - `cmtr-u6ywlqot-async_queue`
- SNS Topic:
  - `cmtr-u6ywlqot-lambda_topic`

## Testing the Deployment

### Send message to SQS
```bash
aws sqs send-message \
  --queue-url https://sqs.eu-west-1.amazonaws.com/422201266106/cmtr-u6ywlqot-async_queue \
  --message-body "Test message" \
  --region eu-west-1
```

### Publish message to SNS
```bash
aws sns publish \
  --topic-arn arn:aws:sns:eu-west-1:422201266106:cmtr-u6ywlqot-lambda_topic \
  --message "Test message" \
  --region eu-west-1
```

### View CloudWatch Logs
```bash
# SQS Handler logs
aws logs tail /aws/lambda/cmtr-u6ywlqot-sqs_handler --follow --region eu-west-1

# SNS Handler logs
aws logs tail /aws/lambda/cmtr-u6ywlqot-sns_handler --follow --region eu-west-1
```

## File Structure
```
task04/
├── japp/
│   ├── src/main/java/com/task04/
│   │   ├── SqsHandler.java          ✅
│   │   └── SnsHandler.java          ✅
│   ├── pom.xml                      ✅
│   └── target/
│       └── task04-1.0.0.jar
├── deployment_resources.json        ✅
├── .syndicate-config-dev/
│   ├── syndicate.yml               ✅
│   └── syndicate_aliases.yml       ✅
└── README.md
```

## Status
✅ **READY FOR DEPLOYMENT**
All code is compiled, packaged, and ready to deploy to AWS.

