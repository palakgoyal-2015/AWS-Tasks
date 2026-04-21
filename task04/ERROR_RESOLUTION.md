# Error Resolution - Duplicate Resource Names

## Problem Encountered
```
ResourceProcessingError occurred: Two resources with equal names were found! 
Name: 'lambda-basic-execution', first resource type: 'iam_policy', second resource type: 'iam_policy'.
Please, rename one of them!
```

## Root Cause
The error occurred because:
1. The Maven Syndicate plugin was auto-generating IAM policy definitions
2. The root `deployment_resources.json` was also defining IAM policies
3. When Syndicate merged these configurations, it found duplicate policy names

## Solution Implemented

### Before (Causing Error)
```json
{
  "lambda-basic-execution": {
    "policy_content": { ... },
    "resource_type": "iam_policy"
  },
  "sqs_queue_policy": {
    "policy_content": { ... },
    "resource_type": "iam_policy"
  },
  "sns_topic_policy": {
    "policy_content": { ... },
    "resource_type": "iam_policy"
  },
  "sqs_handler-role": {
    "custom_policies": [
      "lambda-basic-execution",
      "sqs_queue_policy"
    ]
  },
  ...
}
```

### After (Fixed)
```json
{
  "sqs_handler-role": {
    "predefined_policies": [
      "AWSLambdaSQSQueueExecutionRole"
    ],
    "principal_service": "lambda",
    "resource_type": "iam_role"
  },
  "sns_handler-role": {
    "predefined_policies": [
      "AWSLambdaSNSTopicExecutionRole"
    ],
    "principal_service": "lambda",
    "resource_type": "iam_role"
  },
  "async_queue": {
    "resource_type": "sqs_queue",
    "visibility_timeout": 300
  },
  "lambda_topic": {
    "resource_type": "sns_topic"
  }
}
```

## Key Changes Made

1. **Removed Custom Policies**: Deleted all custom IAM policy definitions from deployment_resources.json
2. **Used AWS Managed Policies**: 
   - Replaced custom "lambda-basic-execution" with AWS Lambda's built-in logging permissions
   - Replaced custom "sqs_queue_policy" with `AWSLambdaSQSQueueExecutionRole`
   - Replaced custom "sns_topic_policy" with `AWSLambdaSNSTopicExecutionRole`
3. **Simplified Configuration**: Only defined non-Lambda resources in deployment_resources.json
4. **Enabled Temp Credentials**: Set `use_temp_creds: true` in syndicate.yml

## Benefits of This Approach

1. **No Duplication**: Each resource defined in only one place
2. **AWS Best Practices**: Uses managed policies instead of custom ones
3. **Less Maintenance**: AWS automatically updates managed policies
4. **Cleaner Code**: Simpler, more readable configuration
5. **Better Security**: AWS managed policies follow security guidelines
6. **Easier Updates**: No need to maintain custom IAM policies

## AWS Managed Policies Used

### AWSLambdaSQSQueueExecutionRole
Grants permissions to:
- `logs:CreateLogGroup`
- `logs:CreateLogStream`
- `logs:PutLogEvents`
- `sqs:ReceiveMessage`
- `sqs:DeleteMessage`
- `sqs:GetQueueAttributes`
- `sqs:ChangeMessageVisibility`

### AWSLambdaSNSTopicExecutionRole
Grants permissions to:
- `logs:CreateLogGroup`
- `logs:CreateLogStream`
- `logs:PutLogEvents`
- `sns:Subscribe`

## Deployment After Fix

```bash
# Clean Maven build
mvn clean package -DskipTests

# Build Syndicate bundle
syndicate build

# Deploy to AWS
syndicate deploy
```

Result: ✅ **SUCCESSFUL** - All resources deployed without errors

## Prevention Tips

1. **Avoid Duplicates**: 
   - Check if AWS has managed policies for your use case
   - Don't redefine policies in both deployment_resources.json and annotations

2. **Use Predefined Policies**:
   - AWS managed policies are regularly updated
   - They follow least-privilege principle

3. **Keep Deployment Clean**:
   - Only define custom resources in deployment_resources.json
   - Let Syndicate auto-generate Lambda definitions

4. **Test Incrementally**:
   - Build locally first
   - Check for compilation errors
   - Review generated configurations

## Commands Used to Resolve

```bash
# 1. Updated deployment_resources.json
# - Removed all IAM policy definitions
# - Added predefined_policies to roles

# 2. Rebuilt Maven project
cd japp
mvn clean package -DskipTests

# 3. Rebuilt Syndicate bundle
cd ..
export SDCT_CONF="$(pwd)/.syndicate-config-dev"
syndicate build

# 4. Redeployed
syndicate deploy
```

## Final Result
✅ All resources deployed successfully
✅ No duplicate resource warnings
✅ Both Lambda functions operational
✅ SQS and SNS triggers configured
✅ CloudWatch logging enabled

## Related Documentation
- `DEPLOYMENT_SUMMARY.md` - Full deployment summary
- `IMPLEMENTATION_GUIDE.md` - Implementation details
- `COMPLETION_CHECKLIST.md` - Task completion status

