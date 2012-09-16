package agents.behaviours;

import utils.Enums.MsgType;
import jade.core.AID;
import jade.lang.acl.MessageTemplate;

public class MessageTemplates {

	public static MessageTemplate priority_request_templ, turn_off_order_templ,
			reduction_order_templ, energy_request_templ;

	public static void init(AID referee) {
		if (priority_request_templ == null) {
			System.out.println("Initializing templates");
			/*
			 * Referee: - timestamp(long) Heater: -
			 * P.name(string):P.type(int):P.level(int):timestamp(long):
			 * heating_energy (int):heating_intervals(int)
			 */
			priority_request_templ = MessageTemplate
					.MatchConversationId(MsgType.PRIORITY_REQUEST.toString());

			/* timestamp(long) */
			turn_off_order_templ = MessageTemplate
					.MatchConversationId(MsgType.TURN_OFF_ORDER.toString());

			/* Referee - timestamp(long):reductionFactor(float):reply(boolean) */
			/*
			 * RefereeWeak -
			 * timestamp(long):reductionFactor(float):reply(boolean)
			 */
			/* Heater - timestamp(long) */
			reduction_order_templ = MessageTemplate
					.MatchConversationId(MsgType.REDUCTION_ORDER.toString());

			/*
			 * Heater -
			 * P.name(string):P.type(int):P.level(int):timestamp(long):energy
			 * (int)
			 */
			/* Referee - timestamp(long):available_energy(int):P.name(string) */
			/* RefereeW - timestamp(long):available_energy(int):P.name(string) */
			energy_request_templ = MessageTemplate
					.MatchConversationId(MsgType.ENERGY_REQUEST.toString());

		}
	}
}
