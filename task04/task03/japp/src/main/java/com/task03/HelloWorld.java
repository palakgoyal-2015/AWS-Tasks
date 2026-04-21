package com.task03;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@LambdaHandler(
    lambdaName = "hello_world",
	roleName = "hello_world-role",
	isPublishVersion = true,
	aliasName = "${lambdas_alias_name}",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
public class HelloWorld implements RequestHandler<Object, Map<String, Object>> {

	public Map<String, Object> handleRequest(Object request, Context context) {
		System.out.println("Hello from lambda");
		
		try {
			Map<String, Object> responseBody = new HashMap<String, Object>();
			responseBody.put("statusCode", 200);
			responseBody.put("message", "Hello from Lambda");
			
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("statusCode", 200);
			resultMap.put("body", new ObjectMapper().writeValueAsString(responseBody));
			resultMap.put("headers", new HashMap<String, String>() {{ put("Content-Type", "application/json"); }});
			
			return resultMap;
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
			
			Map<String, Object> errorMap = new HashMap<String, Object>();
			errorMap.put("statusCode", 500);
			errorMap.put("body", "{\"statusCode\": 500, \"message\": \"Internal server error\"}");
			return errorMap;
		}
	}
}


