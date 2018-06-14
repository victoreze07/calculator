package com;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.Loader;

enum OpType
{
    Add,
    Sub,
    Mul,
    Div,
    Let,
    None
}

class TypeAndExpressions
{
    OpType type;
    String[] expressions;
}

public class App {
	
	final static Logger logger = Logger.getLogger(App.class);
	
    public static void main(String[] args) {
    	try
    	{
    		if(args != null && args.length > 0)
        	{
        		if(args.length > 1)
        		{
        			if(args[1].equalsIgnoreCase("warn"))
        				PropertyConfigurator.configure(Loader.getResource("log4j-warn.properties"));
        			else if(args[1].equalsIgnoreCase("debug"))
        				PropertyConfigurator.configure(Loader.getResource("log4j-debug.properties"));
        			else if(args[1].equalsIgnoreCase("info"))
        				PropertyConfigurator.configure(Loader.getResource("log4j-info.properties"));
        			else
        				PropertyConfigurator.configure(Loader.getResource("log4j-debug.properties"));
        		}
        		else
        			PropertyConfigurator.configure(Loader.getResource("log4j-debug.properties"));
        		String exp = args[0];
                App c = new App();
                System.out.println("Result: "+ c.calculate(exp.trim(), null));
				System.out.println("Hi");
        	}
        	else
        		System.out.println("Please provide input. First argument is expression. Second argument can be warn, info or debug.Second argument is optional.");
    	}    	
    	catch(ArithmeticException ae)
    	{    		
    		logger.error("Error : "+ae);
    		System.out.println("Number is divide by zero. Please check your expression");
    	}
    	catch(Exception e)
    	{
    		logger.error("Error : "+e);
    	}
    }
    
    public int calculate(String expression, Map<String, Integer> map)
    {
    	logger.info("calculate method begin");
        TypeAndExpressions typeAndExpressions = fetchTypeAndExpressionsFromString(expression);
        switch(typeAndExpressions.type)
        {
            case Add:
                int a1 = calculate(typeAndExpressions.expressions[0],map);
                int b1 = calculate(typeAndExpressions.expressions[1],map);
                return a1 + b1;
            case Sub:
                int a2 = calculate(typeAndExpressions.expressions[0],map);
                int b2 = calculate(typeAndExpressions.expressions[1],map);
                return a2 - b2;
            case Mul:
                int a3 = calculate(typeAndExpressions.expressions[0],map);
                int b3 = calculate(typeAndExpressions.expressions[1],map);
                return a3 * b3;
            case Div:
                int a4 = calculate(typeAndExpressions.expressions[0],map);
                int b4 = calculate(typeAndExpressions.expressions[1],map);
                return a4 / b4;              
            case Let:
                int a5 = calculate(typeAndExpressions.expressions[1],map);
                Map<String, Integer> newMap = new HashMap<String, Integer>();
                if(map != null)
                {
                    for (Map.Entry<String, Integer> entry : map.entrySet())
                    {
                        logger.debug(entry.getKey() + "/" + entry.getValue());
                        newMap.put(entry.getKey(), entry.getValue());
                    }
                }
                newMap.put(typeAndExpressions.expressions[0], a5);
                return calculate(typeAndExpressions.expressions[2],newMap);
            case None:
                if(map != null && map.containsKey(typeAndExpressions.expressions[0]))
                {
                    return map.get(typeAndExpressions.expressions[0]).intValue();
                }
                return Integer.parseInt(typeAndExpressions.expressions[0]);
        }
        logger.info("calculate method end");
        return 0;
    }
    
    //This method separates operator and its arguments
    private TypeAndExpressions fetchTypeAndExpressionsFromString(String exp)
    {
    	logger.info("fetchTypeAndExpressionsFromString method begin");
    	
        TypeAndExpressions typeAndExpressions = new TypeAndExpressions();
        if(exp.length() < 4)
        {
        	logger.debug("only expression");
            typeAndExpressions.type = OpType.None;
            typeAndExpressions.expressions = new String[1];
            typeAndExpressions.expressions[0] = exp;
        }
        else
        {
            if(exp.startsWith("add("))
            {
            	logger.debug("add operation");
                typeAndExpressions.type = OpType.Add;
                typeAndExpressions.expressions = getTwoSubexpression(exp.substring(3));
            }
            else if(exp.startsWith("sub("))
            {
            	logger.debug("sub operation");
                typeAndExpressions.type = OpType.Sub;
                typeAndExpressions.expressions = getTwoSubexpression(exp.substring(3));
            }
            else if(exp.startsWith("mult("))
            {
            	logger.debug("mult operation");
                typeAndExpressions.type = OpType.Mul;
                typeAndExpressions.expressions = getTwoSubexpression(exp.substring(4));
            }
            else if(exp.startsWith("div("))
            {
            	logger.debug("div operation");
                typeAndExpressions.type = OpType.Div;
                typeAndExpressions.expressions = getTwoSubexpression(exp.substring(3));
            }
            else if(exp.startsWith("let("))
            {
            	logger.debug("let operation");
                typeAndExpressions.type = OpType.Let;
                typeAndExpressions.expressions = getThreeSubexpression(exp.substring(3));
            }
            else
            {
            	logger.debug("No operation");
                typeAndExpressions.type = OpType.None;
                typeAndExpressions.expressions = new String[1];
                typeAndExpressions.expressions[0] = exp;
            }
        }
        logger.info("fetchTypeAndExpressionsFromString method end");
        return typeAndExpressions;
    }
    
    //This is getting two arguments of operators other than let
    private String[] getTwoSubexpression(String exp)
    {
    	logger.info("getTwoSubexpression method begin");
        int noOfOpenparan = 0;
        String[] expressions = new String[2];
        
        for(int i=0; i<exp.length(); i++)
        {
            if(exp.charAt(i) == '(')
            {
                noOfOpenparan++;
            }
            else if(exp.charAt(i) == ')')
            {
                noOfOpenparan--;
            }
            else if(exp.charAt(i) == ',' && noOfOpenparan == 1)
            {
                //This is mid point we have both sub expression now
                expressions[0] = exp.substring(1, i).trim();
                logger.debug(expressions[0]);
                expressions[1] = exp.substring(i+1, exp.length()-1).trim();
                logger.debug(expressions[1]);
                return expressions;
            }
        }
        logger.info("getTwoSubexpression method end");
        return null;
    }
    
   //This is getting three arguments of operator let
    private String[] getThreeSubexpression(String exp)
    {
    	logger.info("getThreeSubexpression method begin");
        int noOfOpenparan = 0;
        int firstCommaIndex = 0, secondCommaIndex = 0;
        String[] expressions = new String[3];
        
        for(int i=0; i<exp.length(); i++)
        {
            if(exp.charAt(i) == '(')
            {
                noOfOpenparan++;
            }
            else if(exp.charAt(i) == ')')
            {
                noOfOpenparan--;
            }
            else if(exp.charAt(i) == ',' && noOfOpenparan == 1)
            {
                if(firstCommaIndex == 0)
                {
                    firstCommaIndex = i;
                }
                else
                {
                    secondCommaIndex = i;
                    break;
                }
            }
        }
        expressions[0] = exp.substring(1, firstCommaIndex).trim();
        logger.debug(expressions[0]);
        expressions[1] = exp.substring(firstCommaIndex+1, secondCommaIndex).trim();
        logger.debug(expressions[1]);
        expressions[2] = exp.substring(secondCommaIndex+1, exp.length()-1).trim();
        logger.debug(expressions[2]);
        
        logger.info("getThreeSubexpression method end");
        return expressions;
    }
}
