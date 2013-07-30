package com.journwe.utility;

import java.util.HashMap;
import java.util.Map;

import net.dharwin.common.tools.cli.api.CLIContext;
import net.dharwin.common.tools.cli.api.Command;
import net.dharwin.common.tools.cli.api.CommandLineApplication;
import net.dharwin.common.tools.cli.api.annotations.CLIEntry;
import net.dharwin.common.tools.cli.api.exceptions.CLIInitException;

@CLIEntry
public class Client extends CommandLineApplication<JournweCLIContext> {

	public Client() throws CLIInitException {
		super();
	}

    @Override
    protected void shutdown() {
            System.out.println("Shutting down Journwe Utility Client.");
    }

    @Override
    protected CLIContext createContext() {
            return new JournweCLIContext(this);
    }

    
    
	
//	public static void main(String[] args) {
//		System.out.println("Launching the command line client journwe-utility");
//		try {
//			Client client = new Client();
//			client.start();
//			client.shutdown();
//		} catch (CLIInitException e) {
//			e.printStackTrace();
//		}
//		
//	}


}
