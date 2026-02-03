package com.adrian.tradingbot.bot;
import org.springframework.stereotype.Service;

import com.adrian.tradingbot.bot.SignalService;

@Service
public class BotService {

	SignalService signalservice;
	BotService(SignalService signalservice){
		this.signalservice=signalservice;
	}
	
	
}
