package com.hypnotech.airport_pis.beans;

import java.time.LocalDateTime;

public class Flight {
	public Integer flight_id;
	// private Airline airline;
	public Integer airline_id;
	// private Checkin checkin;
	public Integer checkin_id;
	// private Boarding boarding;
	public Integer boarding_id;
	// private Destination destination;
	public Integer destination_id;
	// private Status manual_status;
	public Integer manual_status_id;
	public String flight_number;
	public LocalDateTime flight_takeoff_time;
	public Boolean flight_auto_handle;
	public Integer flight_delay;
}
