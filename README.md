# Trading_bot
Simple algorithmic trading bot with backtesting and live simulation.
Build with Spring Boot(Java) and React (Vite).



## Tech Stack



Backend:

- Java 17

- Spring Boot

- Spring Web

- Spring JDBC

- PostgreSQL

- Binance public REST API



Frontend:

- React

- Vite

- Plain CSS



---



## Project Structure



Backend (Spring Boot):



src/main/java/com/adrian/tradingbot

- api

  - BotController

  - PortfolioController

  - TradeController

  - TrainingResult
- bot

  - BotService

  - BotStateService

  - SignalService

  - TradingService

  - TradingScheduler

- marketdata

  - MarketDataService

- model

  - Candle

  - Holding

  - Trade

- persistence

  - TradeRepository
 
  - HoldingRepository
 
  - PortfolioRepository

- portfolio

  - PortfolioService

  - PortfolioSnapshot

- config

  - WebClient configuration



Resources:

- src/main/resources/db/schema.sql



Frontend (React + Vite):



frontend/

- src/

  - components/

  - services/

  - App.jsx

  - main.jsx

- vite.config.js



---



## How the System Works



1. Market data is fetched from the Binance public API (klines endpoint).

2. The bot calculates fast and slow moving averages.

3. A signal is generated:

   - BUY: fast MA crosses above slow MA

   - SELL: fast MA crosses below slow MA

   - HOLD: no crossover

4. Trades are simulated (no real orders).

5. Portfolio state and trades are stored in PostgreSQL.

6. The frontend polls the backend and displays:

   - Bot status

   - Portfolio state

   - Trade history



---



## Backtesting



Backtesting uses historical candle data.

The bot iterates candle by candle and evaluates the strategy

as if it were running in real time.



No future data is used during decision making.



---



## Running the Backend



Requirements:

- Java 17

- PostgreSQL



Steps:

1. Create PostgreSQL database

2. Update application.properties

3. Run:
   
   -Backend runs on https://localhost:8080

   -Frontend runs on http://localhost:5173

   Frontend requirments:
   
       -Node.js 18+
   
       -command prompt:
   
            -cd frontend
            -npm install
            -npm run dev
