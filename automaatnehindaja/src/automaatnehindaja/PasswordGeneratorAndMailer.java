package automaatnehindaja;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import config.Config;
import config.ConfigException;

public class PasswordGeneratorAndMailer {

	public PasswordGeneratorAndMailer() {
		super();
	}

	private static SecureRandom random = new SecureRandom();

	protected String generatePassword() {
		char[] allowedCharacters = { 'a', 'b', 'c', 'd', 'f', 'e', 'g', 'h',
				'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
				'u', 'v', 'z', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		StringBuffer password = new StringBuffer();
		for (int i = 0; i < 8; i++) {
			password.append(allowedCharacters[random
					.nextInt(allowedCharacters.length)]);
		}
		return password.toString();
	}

	protected static String sha1(String input) throws NoSuchAlgorithmException {
		MessageDigest mDigest = MessageDigest.getInstance("SHA1");
		byte[] result = mDigest.digest(input.getBytes());
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < result.length; i++) {
			sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16)
					.substring(1));
		}

		return sb.toString();
	}

	protected void emailPassword(String to, String passwordToSend) {

		try {
			
			Config config = new Config(PasswordGeneratorAndMailer.class.getClassLoader().getResource("").getPath() + "../../config/Config.cfg");
			final String username = config.getString("emailUsername");
			final String password = config.getString("emailPassword");
			
			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", config.getString("emailHost"));
			props.put("mail.smtp.port", config.getString("emailPort"));

			Session session = Session.getInstance(props,
					new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(username, password);
						}
					});

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(to));
			message.setSubject(config.getString("emailSubject"));
			message.setText(config.getString("emailHeader") + "\n"
					+ config.getString("emailUsernameRow") + to + "\n"
					+ config.getString("emailPasswordRow") + passwordToSend + "\n" 
					+ config.getString("emailFooter") + "\n"
					+ config.getString("emailSiteAddr"));
			Transport.send(message);

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		} catch (ConfigException e) {
			e.printStackTrace();
		}
	}

	protected void sendAsyncEmails(final List<String> emailAddresses,
			final List<String> passwords) {
		new Thread(new Runnable() {

			@Override
			public void run() {

				for (int i = 0; i < emailAddresses.size(); i++) {
					emailPassword(emailAddresses.get(i), passwords.get(i));
				}

			}
		}).start();
	}
}
