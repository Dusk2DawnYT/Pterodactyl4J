package com.mattmalec.pterodactyl4j.client.entities.impl;

import com.mattmalec.pterodactyl4j.PteroActionImpl;
import com.mattmalec.pterodactyl4j.client.entities.ClientServer;
import com.mattmalec.pterodactyl4j.client.entities.Cron;
import com.mattmalec.pterodactyl4j.client.entities.Schedule;
import com.mattmalec.pterodactyl4j.client.managers.ScheduleAction;
import com.mattmalec.pterodactyl4j.entities.PteroAction;
import com.mattmalec.pterodactyl4j.requests.Route;
import com.mattmalec.pterodactyl4j.utils.CronUtils;
import org.json.JSONObject;

public class EditScheduleImpl implements ScheduleAction {

	private ClientServer server;
	private Schedule schedule;
	private PteroClientImpl impl;

	private String name;
	private Boolean active;
	private Cron cron;
	private String minute;
	private String hour;
	private String dayOfWeek;
	private String dayOfMonth;

	public EditScheduleImpl(ClientServer server, Schedule schedule, PteroClientImpl impl) {
		this.server = server;
		this.schedule = schedule;
		this.impl = impl;
	}
	@Override
	public ScheduleAction setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public ScheduleAction setActive(boolean active) {
		this.active = active;
		return this;
	}

	@Override
	public ScheduleAction setCron(Cron cron) {
		this.cron = cron;
		return this;
	}

	@Override
	public ScheduleAction setCronExpression(String expression) {
		this.cron = CronUtils.ofExpression(expression);
		return this;
	}

	@Override
	public ScheduleAction setMinute(String minute) {
		this.minute = minute;
		return this;
	}

	@Override
	public ScheduleAction setHour(String hour) {
		this.hour = hour;
		return this;
	}

	@Override
	public ScheduleAction setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
		return this;
	}

	@Override
	public ScheduleAction setDayOfMonth(String dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
		return this;
	}

	@Override
	public PteroAction<Schedule> build() {
		JSONObject json = new JSONObject()
				.put("name", name == null ? schedule.getName() : name)
				.put("is_active", active == null ? schedule.isActive() : active)
				.put("minute", minute == null ? ((cron == null || cron.getMinute() == null) ? schedule.getCron().getMinute() : cron.getMinute()) : minute)
				.put("hour", hour == null ? ((cron == null || cron.getHour() == null) ? schedule.getCron().getHour() : cron.getHour()) : hour)
				.put("day_of_week", dayOfWeek == null ? ((cron == null || cron.getDayOfWeek() == null) ? schedule.getCron().getDayOfWeek() : cron.getDayOfWeek()) : dayOfWeek)
				.put("day_of_month", dayOfMonth == null ? ((cron == null || cron.getDayOfMonth() == null) ? schedule.getCron().getDayOfMonth() : cron.getDayOfMonth()) : dayOfMonth);
		return PteroActionImpl.onExecute(() -> {
			Route.CompiledRoute route = Route.Schedules.UPDATE_SCHEDULE.compile(server.getUUID().toString(), schedule.getId()).withJSONdata(json);
			JSONObject obj = impl.getRequester().request(route).toJSONObject();
			return new ScheduleImpl(obj, server, impl);
		});
	}
}
