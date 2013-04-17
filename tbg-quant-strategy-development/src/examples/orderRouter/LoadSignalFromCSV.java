/*
 * TBG-QUANT
 * The Bonnot Gang Quantitative Trading Framework 
 *  
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 * 
 */
package examples.orderRouter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.tbg.core.model.Order;
import com.tbg.core.model.Security;
import com.tbg.core.model.Symbol;
import com.tbg.core.model.types.Currency;
import com.tbg.core.model.types.OrderSide;
import com.tbg.core.model.types.OrderTIF;
import com.tbg.core.model.types.OrderType;
import com.tbg.core.model.types.SecurityType;

/**
 * CSV Signal reader <br>
 * <br>
 * CSV file format:<br>
 * 
 * Symbol;SecurityType;Currency;OrderSide;Quantity;OrderType;PriceLimit;Exchange
 * 
 * ex.
 * 
 * IBM;STK;USD;BUY;1000;MARKET;0;SMART
 * GOOG;STK;USD;BUY;200;LIMIT;619.25;SMART
 * 
 * Check the file order_gld2013-02.07.csv as an example. 
 * 
 * <br>
 * <b>History:</b><br>
 *  - [02/feb/2013] Created. (Alberto Sfolcini)<br>
 *
 *  @author a.sfolcini@gmail.com
 */
public class LoadSignalFromCSV {
	
	protected final static Logger log = Logger.getLogger(LoadSignalFromCSV.class);
	
	private String csvFile = "";
	private String csvSeparator = ","; // standard for CSV
	private Security security = new Security();
	private Order order = new Order();
	private boolean success = false;
	
	/**
	 * Constructor
	 * @param csvFile
	 */
	public LoadSignalFromCSV(String csvFile){
		this.csvFile = csvFile;
		init();
	}
	public LoadSignalFromCSV(String csvFile,String csvSeparator){
		this.csvFile = csvFile;
		this.csvSeparator = csvSeparator;
		init();
	}
	
	
	
	private void init(){
		BufferedReader input = null;
		File myCsv = new File(this.csvFile);
		if (myCsv.exists()){
			try {
				input = new BufferedReader(new FileReader(csvFile));
				String str;
				try{
					while ((str = input.readLine()) != null) {						
						StringTokenizer st = new StringTokenizer(str, csvSeparator);
						
						if (st.countTokens()!=8){
							log.error("CSV File Format is WRONG, please check it out!");
							return;
						}
						
						
						while(st.hasMoreTokens()){
							String security_symbol 	= st.nextToken().replaceAll("\"", "");;
							String security_type	= st.nextToken().replaceAll("\"", "");
							String security_currency= st.nextToken().replaceAll("\"", "");;
							String order_side		= st.nextToken().replaceAll("\"", "");;
							String order_qty		= st.nextToken().replaceAll("\"", "");;
							String order_type		= st.nextToken().replaceAll("\"", "");;
							String order_limitPrice	= st.nextToken().replaceAll("\"", "");;
							String order_exchange	= st.nextToken().replaceAll("\"", "");;

							security.setSymbol(new Symbol(security_symbol));
							security.setSecurityType(SecurityType.valueOf(security_type));
							security.setCurrency(Currency.valueOf(security_currency));
							security.setExchange(order_exchange);
													
							order.setOrderType(OrderType.valueOf(order_type));
							order.setQuantity(Integer.valueOf(order_qty));
							order.setSecurity(security);
							order.setOrderSide(OrderSide.valueOf(order_side));
							order.setTimeInForce(OrderTIF.DAY); // this is not from CSV
							double limitPrice = Double.valueOf(order_limitPrice);
							if (limitPrice>0)
								order.setLimitPrice(limitPrice);
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				
				success = true;
			} catch (FileNotFoundException e) {e.printStackTrace();}
			finally{
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}			
			}
			
		}else{
			success = false;
		}
			
	} // end of init()
			
		
	public boolean isReady(){
		return success;
	}
	
	public Order getOrder(){
		return order;
	}		
	
	public Security getSecurity(){
		return security;
	}
		
		
	

}
