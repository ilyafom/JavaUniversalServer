package UserAccountWork;

import java.util.Random;

import DBWork.DataBaseAccess;
import EmailWork.EmailWorker;

public class Password—hangeWork {

	public static String generateSinglePassword() {
		Random rand = new Random();

		String code = "";
		for (int i = 0; i < 9; i++) {

			if (rand.nextInt(100) > 40) {

				code += "" + (char) (rand.nextInt(25) + 97);

			} else {
				code += "" + (rand.nextInt(9));

			}

		}

		return code;
	}

	public static boolean addSinglePassword(String code, UserAccount acc) {
		DataBaseAccess db = new DataBaseAccess();

		db.addSinglePasswordForAccount(code, acc);

		String email = db.getMailFromDb(acc);

		return EmailWorker.sendMessageToEmail(email, code);

	}

}
