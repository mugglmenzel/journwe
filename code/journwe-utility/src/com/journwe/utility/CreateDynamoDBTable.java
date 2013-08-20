package com.journwe.utility;

import java.util.Properties;

import net.dharwin.common.tools.cli.api.Command;
import net.dharwin.common.tools.cli.api.CommandResult;
import net.dharwin.common.tools.cli.api.annotations.CLICommand;
import net.dharwin.common.tools.cli.api.console.Console;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.beust.jcommander.Parameter;

@CLICommand(name = "create-dynamodb-table", description = "-e environment -t tableName -h hashID -r rangeID")
public class CreateDynamoDBTable extends Command<JournweCLIContext> {
	
	@Parameter(names={"-e", "--environment"}, description="Must be either 'dev' or 'prod'", required=true)
    private String _environment;
	
	@Parameter(names={"-t", "--tableName"}, description="Name of the new DynamoDB table.", required=true)
    private String _tableName;
   
    @Parameter(names={"-h", "--hashID"}, description="Table HashID (type=String).", required=true)
    private String _hashID;
    
    @Parameter(names={"-r", "--rangeID"}, description="Table RangeID (optional, type=String).", required=false)
    private String _rangeID;


	@Override
	public CommandResult innerExecute(JournweCLIContext context) {

			String propertiesFileName = "aws.dev.properties";
			if(_environment.equalsIgnoreCase("prod"))
				propertiesFileName = "aws.prod.properties";
			Properties prop = new Properties();
			String accesskey = "NOT_SET";
			String secret = "NOT_SET";
			
			try {
				// load a properties file from class path, inside static method
				prop.load(CleanJournweDBCommand.class.getClassLoader()
						.getResourceAsStream(propertiesFileName));
				accesskey = prop.getProperty("accesskey");
				secret = prop.getProperty("secret");
				BasicAWSCredentials credentials = new BasicAWSCredentials(
						accesskey, secret);
				AmazonDynamoDBClient dynamoDB = new AmazonDynamoDBClient(
						credentials);
				String endpoint = prop.getProperty("endpoint");
				dynamoDB.setEndpoint(endpoint);
				
				CreateTableRequest createTableRequest = new CreateTableRequest().
						withTableName(_tableName)
		                .withKeySchema(new KeySchemaElement()
		                	.withAttributeName(_hashID)
		                	.withKeyType(KeyType.HASH))
		                	.withAttributeDefinitions(new AttributeDefinition()
		                		.withAttributeName(_hashID)
		                		.withAttributeType(ScalarAttributeType.S)
		                	)
		                	.withProvisionedThroughput(new ProvisionedThroughput()
		                	.withReadCapacityUnits(1L)
		                	.withWriteCapacityUnits(1L)
		                );
		         if(_rangeID!=null && !_rangeID.isEmpty()) {
		        	 createTableRequest = new CreateTableRequest().
								withTableName(_tableName)
				                .withKeySchema(new KeySchemaElement()
				                	.withAttributeName(_hashID)
				                	.withKeyType(KeyType.HASH)
				                	)
				                	.withAttributeDefinitions(new AttributeDefinition()
				                		.withAttributeName(_hashID)
				                		.withAttributeType(ScalarAttributeType.S)
				                	)
				                .withKeySchema(new KeySchemaElement()
				                	.withAttributeName(_rangeID)
				                	.withKeyType(KeyType.RANGE)
				                	)
				                	.withAttributeDefinitions(new AttributeDefinition()
				                		.withAttributeName(_rangeID)
				                		.withAttributeType(ScalarAttributeType.S)
				                	)
				                	.withProvisionedThroughput(new ProvisionedThroughput()
				                	.withReadCapacityUnits(1L)
				                	.withWriteCapacityUnits(1L)
				                );
		         }
		            TableDescription createdTableDescription = dynamoDB.createTable(createTableRequest).getTableDescription();
		            Console.info("Created Table in "+_environment+" environment: " + createdTableDescription);
			} catch (Exception e) {
				e.printStackTrace();
				return CommandResult.ERROR;
			}
		
		return CommandResult.OK;
	}

}