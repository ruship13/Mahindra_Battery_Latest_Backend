package com.ats.mahindrabattery.serviceimpl;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ats.mahindrabattery.entity.GenerateManualRetrievalOrderEntity;
import com.ats.mahindrabattery.entity.YokogawaTemperatureDetailsEntity;
import com.ats.mahindrabattery.repository.YokogawaTemperatureDetailsRepository;
import com.ats.mahindrabattery.service.YokogawaTemperatureDetailsService;

@Service
public class YokogawaTemperatureDetailsServiceImpl implements YokogawaTemperatureDetailsService {

	@Autowired
	private YokogawaTemperatureDetailsRepository yokogawaTemperatureDetailsRepository;

	@SuppressWarnings("unused")
	@Override
	public YokogawaTemperatureDetailsEntity getTemperatureDetailsOfCurrentDate() {

		try {
			int i = 0;
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String currentDateTime = dateFormat.format(date);

			List<YokogawaTemperatureDetailsEntity> ordersToday = yokogawaTemperatureDetailsRepository
					.findByCreatedDateTimeBetween(currentDateTime + " " + "00:00:00",
							currentDateTime + " " + "23:59:59");
			if (!ordersToday.isEmpty()) {
				for (; i < ordersToday.size(); i++) {

				}
				return ordersToday.get(i-1);
			} else if (ordersToday.isEmpty()) {
				return null;
			}
			return ordersToday.get(0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;

	}

}
