tbg-Quant algorithmic trading framework
-------------------------------------------------------------------------------------

This project shows you how to build trading strategies using tbg-Quant algorithmic trading framework. 
<br><br>
Dependence library: <strong>tbg-Quant-v1.1beta5</strong>

Contents
-------------------------------------------------------------------------------------
<i><b>examples.skeleton</b></i>
<ul>
  <b>Skeleton.java</b><br>
  Use this code like a starting point to build your unique strategy.
</ul>

<i><b>examples.helloworld</b></i>
<ul>
  <b>HelloBogus.java</b><br>
  Trading system placing orders every X events, it uses BogusMarketDataFeed (fake market data).
  <br>
  <b>MultiBogus.java</b><br>
  Like HelloBogus but working on multiple symbols.
  <br>
  <b>HelloYahoo.java</b><br>
  How to use Yahoo data usign YahooMarketDataFeed.
  <br>
  <b>HelloSiddhi.java</b><br>
  How to use Siddhi CEP Provider.<br>
  Siddhi CEP is a lightweight, easy-to-use Open Source Complex Event Processing Engine (CEP). Siddhi CEP processes events which are triggered by various 
  event sources and notifies appropriate complex events according to the user specified queries. Siddhi is an alternative to Esper CEP. 
  <br>
  <b>HelloEsper.java</b><br>
  How to use ESPER CEP Provider.<br>
  ESPER is an opensource CEP distributed under GPL2.0 license.Since tbg-Quant is proprietary software the Esper library is not bundled. Be sure to add the Esper library in the classpath. You can download Esper library from here: http://esper.codehaus.org/
  <br>
  <b>HelloIB.java</b><br>
  How to connect to InteractiveBrokers. You can connect to IB throught TWS Client or IBGateway. Be sure to include the IB API library in the classpath before running the example.
  <br>
  <b>HelloTBG.java</b><br>
  Shows how to access to online & offline TBG Historical data (raw 1 minute candle).
  <br>
  
</ul>

<i><b>examples.interactivebrokers</b></i>
<ul>
  <b>IB_CEP_Esper.java</b><br>
  Complex Event Processing with InteractiveBrokers.<br>
  Since IB is not providing candles in this example we use the CEP to build 1 minute candles.<br>
  <i>WARNING</i>: Be sure to include Esper library and IB API in the classpath.
  <br>
  <b>IB_TS_Esper.java</b><br>
   InteractiveBroker trading system example. It uses InteractiveBrokers as a MarketDataFeed and Broker.<br>
   <i><strong>BE AWARE NOT TO USE THIS EXAMPLE ON YOUR IB REAL ACCOUNT!! Orders will be executed!</strong></i><br>
   <i>WARNING</i>: Be sure to include Esper library and IB API in the classpath.
  <br>
  <b>IB_CancelingOrders_Esper.java</b><br>
   TickEvent are processed by Esper engine and printed out every 5 seconds with average price. It sends a limit order and then cancel it.<br>
   <i>WARNING</i>: Be sure to include Esper library and IB API in the classpath.
</ul>

<i><b>examples.TripleMAStrategy</b></i>
<ul>
  <b>TripleMAStartegy.java</b><br>
  Full implementation of a simple trading system using a triple moving average cross. Works with Yahoo weekly data.
</ul>

<i><b>examples.orderRouter</b></i>
<ul>
  <b>OrderRouter.java</b><br>
  Routes orders received via CSV files to InteractiveBrokers account.<br>
  <i>WARNING</i>: Be sure to include IB API in the classpath.
</ul>

<i><b>examples.ToM</b></i>
<ul>
  <b>ToM_Strategy.java</b><br>
  This strategy tries to exploit the well known Turn of the Month effect.<br>
  Returns around 4% profit per year on SPY in the last 12 years.
</ul>

<i><b>examples.seasonal</b></i>
<ul>
  <b>NovApr_Strategy.java</b><br>
  This strategy tries to exploit the November-April seasonal effect.<br>
  Returns around 6% profit per year on XOM,LMT and BHP in more than 20 years.
</ul>

<i><b>utils</b></i>
<ul>
  <b>YahooDataSynchronizer.java</b><br>
  Synchronizes Yahoo historical data between more symbols and write a csv file as output.<br>
  Output data (csv format) includes date,X_Open,X_High,X_Low,X_Close,X_Volume,X_Yield , where X is the symbol.
</ul>




Contacts
-------------------------------------------------------------------------------------
eMail     : Alberto Sfolcini <a.sfolcini@gmail.com><br>
LinkedIn  : http://www.linkedin.com/in/albertosfolcini<br>
Feel free to contact me at any time.<br>


Credits
-------------------------------------------------------------------------------------
tbg-Quant is a proprietary product of The Bonnot Gang  www.thebonnotgang.com<br>
GitHub   : https://github.com/tbg-quant/tbg-quant.git<br>
LinkedIn : http://www.linkedin.com/company/the-bonnot-gang/





