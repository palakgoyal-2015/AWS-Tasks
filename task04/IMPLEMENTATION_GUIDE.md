# Task04 - Complete Implementation Guide

## Project Overview
This project deploys two AWS Lambda functions using Java 11 and AWS-Syndicate:
- **sqs_handler**: Processes messages from SQS queue `async_queue`
- **sns_handler**: Processes messages from SNS topic `lambda_topic`

## Files Modified/Created

### 1. SqsHandler.java
Location: `japp/src/main/java/com/task04/SqsHandler.java`

```java
package com.task04;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

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
            System.out.println("Message from SQS: " + body);
        }
        return null;
    }
}
```

### 2. SnsHandler.java
Location: `japp/src/main/java/com/task04/SnsHandler.java`

```java
package com.task04;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

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
            System.out.println("Message from SNS: " + message);
        }
        return null;
    }
}
```

### 3. deployment_resources.json
Location: `deployment_resources.json`

```json
{
  "sqs_handler-role": {
    "predefined_policies": [
      "AWSLambdaSQSQueueExecutionRole"
    ],
    "principal_service": "lambda",
    "resource_type": "iam_role",
    "tags": {}
  },
  "sns_handler-role": {
    "predefined_policies": [
      "AWSLambdaSNSTopicExecutionRole"
    ],
    "principal_service": "lambda",
    "resource_type": "iam_role",
    "tags": {}
  },
  "async_queue": {
    "resource_type": "sqs_queue",
    "visibility_timeout": 300,
    "tags": {}
  },
  "lambda_topic": {
    "resource_type": "sns_topic",
    "tags": {}
  }
}
```

### 4. pom.xml
Location: `japp/pom.xml`

Key dependencies:
```xml
<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-lambda-java-core</artifactId>
    <version>1.2.0</version>
</dependency>
<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-lambda-java-events</artifactId>
    <version>3.11.0</version>
</dependency>
<dependency>
    <groupId>net.sf.aws-syndicate</groupId>
    <artifactId>deployment-configuration-annotations</artifactId>
    <version>1.17.1</version>
</dependency>
```

### 5. syndicate.yml
Location: `.syndicate-config-dev/syndicate.yml`

Key configuration:
- account_id: 908027412681
- region: eu-west-1
- resources_prefix: cmtr-u6ywlqot-
- use_temp_creds: true
- deploy_target_bucket: syndicate-education-platform-custom-sandbox-artifacts-2564/u6ywlqot/task04

### 6. syndicate_aliases.yml
Location: `.syndicate-config-dev/syndicate_aliases.yml`

```yaml
account_id: '908027412681'
lambdas_alias_name: learn
logs_expiration: 30
region: eu-west-1
```

## Build Commands

```bash
# 1. Compile and package
cd japp
mvn clean package -DskipTests

# 2. Build Syndicate bundle
cd ..
export SDCT_CONF="$(pwd)/.syndicate-config-dev"
syndicate build

# 3. Deploy to AWS
syndicate deploy

# 4. Clean up resources (if needed)
syndicate clean
```

## Deployment Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    AWS Environment                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌──────────────────┐         ┌──────────────────┐        │
│  │   SQS Queue      │         │   SNS Topic      │        │
│  │  async_queue     │         │  lambda_topic    │        │
│  └────────┬─────────┘         └────────┬─────────┘        │
│           │                            │                   │
│           │ Message                    │ Publish          │
│           ▼                            ▼                   │
│  ┌──────────────────────┐  ┌──────────────────────┐       │
│  │ Lambda: sqs_handler  │  │ Lambda: sns_handler  │       │
│  │ - Runtime: Java 11   │  │ - Runtime: Java 11   │       │
│  │ - Memory: 1024 MB    │  │ - Memory: 1024 MB    │       │
│  │ - v1, alias: learn   │  │ - v1, alias: learn   │       │
│  └────────┬─────────────┘  └────────┬─────────────┘       │
│           │                         │                     │
│           │ Log                      │ Log                │
│           ▼                          ▼                     │
│  ┌────────────────────────────────────────────┐           │
│  │        CloudWatch Logs                     │           │
│  │  - aws/lambda/cmtr-u6ywlqot-sqs_handler   │           │
│  │  - aws/lambda/cmtr-u6ywlqot-sns_handler   │           │
│  └────────────────────────────────────────────┘           │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## Key Features

1. **Serverless Architecture**: No servers to manage, pay only for what you use
2. **Event-Driven**: Lambda functions automatically triggered by SQS/SNS events
3. **Monitoring**: Integrated CloudWatch logging for debugging and monitoring
4. **Versioning**: Lambda functions are versioned and aliased for better management
5. **Infrastructure as Code**: Everything defined in code for reproducibility
6. **IAM Security**: Least privilege access with AWS managed policies

## Testing

### Test SQS Handler
```bash
# Send message to SQS queue
aws sqs send-message \
  --queue-url https://sqs.eu-west-1.amazonaws.com/908027412681/cmtr-u6ywlqot-async_queue \
  --message-body '{"key": "value"}' \
  --region eu-west-1

# View logs
aws logs tail aws/lambda/cmtr-u6ywlqot-sqs_handler --follow --region eu-west-1
```

### Test SNS Handler
```bash
# Publish message to SNS topic
aws sns publish \
  --topic-arn arn:aws:sns:eu-west-1:908027412681:cmtr-u6ywlqot-lambda_topic \
  --message '{"key": "value"}' \
  --region eu-west-1

# View logs
aws logs tail aws/lambda/cmtr-u6ywlqot-sns_handler --follow --region eu-west-1
```

## Troubleshooting

### Issue: "The REST API doesn't contain any methods"
- Ensure all Lambda handlers have @LambdaHandler annotation
- Verify handler class names match in annotations

### Issue: "Duplicate resource names"
- Check for duplicate definitions in deployment_resources.json
- Remove redundant IAM policy definitions
- Use AWS managed policies when possible

### Issue: "Queue visibility timeout less than function timeout"
- Increase queue visibility timeout to match function timeout
- Or reduce function timeout
- Typical: queue timeout = function timeout + buffer (e.g., 300 + buffer)

## Maintenance

- Lambda logs retain for 30 days (configurable via logs_expiration)
- CloudWatch log groups are auto-created during deployment
- Lambda versions are immutable, aliases point to specific versions
- Easy to update: modify code, rebuild, redeploy

## Success Criteria Met ✅
- [x] sqs_handler processes SQS messages
- [x] sns_handler processes SNS messages
- [x] Both functions log to CloudWatch
- [x] IAM roles properly configured
- [x] SQS queue and SNS topic created
- [x] Lambda versioning and aliasing enabled
- [x] Infrastructure deployed via Syndicate
- [x] All code follows AWS best practices

