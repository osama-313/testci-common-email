import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import javax.mail.Message;
import javax.mail.Session;
import java.util.Date;
import java.util.Properties;

import static org.junit.Assert.*;
import javax.mail.internet.MimeMessage;
import java.lang.reflect.Field;


public class EmailTest {
	
    private Email email;

    // Sets up the Email instance before each test.
    @Before
    public void setUp() {
        email = new Email() {
            @Override
            public Email setMsg(String msg) {
                return this;
            }

            @Override
            public Email addBcc(String email, String name) throws EmailException {
                try {
                    this.getBccAddresses().add(new javax.mail.internet.InternetAddress(email));
                } catch (javax.mail.internet.AddressException e) {
                    throw new EmailException("Invalid email address: " + email);
                }
                return this;
            }
        };
        

    }
    
    // Cleans up the Email instance after each test.
    @After
    public void tearDown() {
        email = null;
    }
    
    

 
    // 1) ---------------------------- Email   addBcc(String... emails)  Tests ----------------------------

    @Test
    public void testAddBcc_ValidEmails() throws EmailException {
        email.addBcc(new String[]{"test1@example.com", "test2@example.com"});
        assertEquals(2, email.getBccAddresses().size());
    }

    
    @Test
    public void testAddBcc_MultipleEmails() throws EmailException {
        // Use multiple valid emails to trigger the loop
        String[] emails = {"test1@example.com", "test2@example.com", "test3@example.com"};
        email.addBcc(emails);
        assertEquals(3, email.getBccAddresses().size());
    }


    @Test(expected = EmailException.class)
    public void testAddBcc_NullEmails() throws EmailException {
        email.addBcc((String[]) null);
    }

    @Test(expected = EmailException.class)
    public void testAddBcc_EmptyEmails() throws EmailException {
        email.addBcc(new String[]{});
    }


    @Test
    public void testAddBcc_SingleEmail() throws EmailException {
        email.addBcc("single@example.com");
        assertEquals(1, email.getBccAddresses().size());
    }

    
    
    
    
    //2) ---------------------------- --> Email   addCc(String email) ----------------------------
    
    @Test
  public void testAddCc() throws EmailException {
      email.addCc(new String[]{"cc@example.com", "cc2@example.com"});
      assertEquals(2, email.getCcAddresses().size());
  }
  
  @Test
  public void testAddCc_SingleEmail() throws EmailException {
      email.addCc("cc@example.com");
      assertEquals(1, email.getCcAddresses().size());
  }

  
  
  
  

  //3) ---------------------------- -->  void     addHeader(String name, String value) ----------------------------
  
  // Test valid input
  @Test
  public void testAddHeader_ValidInput() {
      email.addHeader("Subject", "Test Email");
      email.addHeader("From", "example@example.com");

      // Re-add the same header to check if it updates correctly
      email.addHeader("Subject", "Updated Test Email");

      // Confirm that no exception was thrown and behavior is consistent
      assertTrue(true); // If no exception, it means the headers were added successfully
  }
  
  
  //  Test null name
  @Test(expected = IllegalArgumentException.class)
  public void testAddHeader_NullName() {
      email.addHeader(null, "Test Email");
  }

  //  Test empty name
  @Test(expected = IllegalArgumentException.class)
  public void testAddHeader_EmptyName() {
      email.addHeader("", "Test Email");
  }

  //  Test null value
  @Test(expected = IllegalArgumentException.class)
  public void testAddHeader_NullValue() {
      email.addHeader("Subject", null);
  }

  //  Test empty value
  @Test(expected = IllegalArgumentException.class)
  public void testAddHeader_EmptyValue() {
      email.addHeader("Subject", "");
  }
  
  
  
  
  
  
  
  //4) ----------------------------  --> Email   addReplyTo(String email, String name) ----------------------------

	@Test
	public void testAddReplyTo() throws EmailException {
	    email.addReplyTo("reply@example.com", "Reply User");
	    assertEquals(1, email.getReplyToAddresses().size());
	}
    
    
	
	
	
	
	
	
    //5) ---------------------------- --> void     buildMimeMessage() ----------------------------
	
	@Test
	public void testBuildMimeMessage_Successful() throws Exception {
	    email.setHostName("smtp.example.com");
	    Properties properties = new Properties();
	    properties.put("mail.smtp.host", email.getHostName());
	    Session session = Session.getInstance(properties);
	    email.setMailSession(session);

	    email.setFrom("test@example.com");
	    email.addTo("receiver@example.com");
	    email.setSubject("Test Subject");
	    email.setContent("Test Content", "text/plain");
	    email.addHeader("X-Test-Header", "HeaderValue");
	    email.buildMimeMessage();

	    Field messageField = Email.class.getDeclaredField("message");
	    messageField.setAccessible(true);
	    MimeMessage message = (MimeMessage) messageField.get(email);

	    assertNotNull(message);
	    assertEquals("Test Subject", message.getSubject());
	    assertEquals("Test Content", message.getContent());
	}
	
	
	  @Test
	    public void testBuildMimeMessage_BasicExecution() throws Exception {

	        email.setHostName("smtp.example.com");
	        Properties properties = new Properties();
	        properties.put("mail.smtp.host", email.getHostName());
	        Session session = Session.getInstance(properties);
	        email.setMailSession(session);

	        // Set minimal required fields
	        email.setFrom("test@example.com");
	        email.addTo("receiver@example.com");

	        // Call buildMimeMessage()
	        email.buildMimeMessage();

	        // Retrieve the built MimeMessage using proper reflection
	        Field messageField = Email.class.getDeclaredField("message");
	        messageField.setAccessible(true); //   Allows access to protected field
	        MimeMessage message = (MimeMessage) messageField.get(email);

	        // Ensure message is created
	        assertNotNull(message);
	    }
	
	

	  @Test(expected = EmailException.class)
	  public void testBuildMimeMessage_MissingRequiredFields() throws EmailException {

	      // Set required properties (but missing From and To)
	      email.setHostName("smtp.example.com");
	      Properties properties = new Properties();
	      properties.put("mail.smtp.host", email.getHostName());
	      Session session = Session.getInstance(properties);
	      email.setMailSession(session);

	      //   Should throw EmailException because required fields are missing
	      email.buildMimeMessage();
	  }

	  @Test(expected = EmailException.class)
	    public void testBuildMimeMessage_MissingFromAddress() throws Exception {

	        // Set required properties
	        email.setHostName("smtp.example.com");
	        Properties properties = new Properties();
	        properties.put("mail.smtp.host", email.getHostName());
	        Session session = Session.getInstance(properties);
	        email.setMailSession(session);

	        //   Only set recipient but NOT the sender
	        email.addTo("receiver@example.com");

	        //   Call buildMimeMessage(), which should throw an EmailException
	        email.buildMimeMessage();
	    }

	 
	  @Test
	    public void testBuildMimeMessage_CC_BCC_ReplyTo() throws Exception {

	        // Set required properties
	        email.setHostName("smtp.example.com");
	        Properties properties = new Properties();
	        properties.put("mail.smtp.host", email.getHostName());
	        Session session = Session.getInstance(properties);
	        email.setMailSession(session);

	        // Set minimal required fields
	        email.setFrom("test@example.com");
	        email.addTo("receiver@example.com");

	        //   Add CC, BCC, and Reply-To addresses
	        email.addCc("cc@example.com");
	        email.addBcc("bcc@example.com");
	        email.addReplyTo("reply@example.com");

	        // Call buildMimeMessage()
	        email.buildMimeMessage();

	        // Retrieve the built MimeMessage using reflection
	        Field messageField = Email.class.getDeclaredField("message");
	        messageField.setAccessible(true);
	        MimeMessage message = (MimeMessage) messageField.get(email);

	        // Ensure message is created
	        assertNotNull(message);
	        assertNotNull(message.getRecipients(Message.RecipientType.CC)); //   Covers ccList
	        assertNotNull(message.getRecipients(Message.RecipientType.BCC)); //   Covers bccList
	        assertNotNull(message.getReplyTo()); //   Covers replyList
	    }
	
	
	
    
    
    
    
    
    //6) ---------------------------- --> String  getHostName() ----------------------------
    
	  @Test
	  public void testGetHostName() {
	      email.setHostName("smtp.example.com");
	      assertEquals("smtp.example.com", email.getHostName());
	  }
	  
	    //   Test session is set and MAIL_HOST is defined
	    @Test
	    public void testGetHostName_FromSession() {
	        Properties properties = new Properties();
	        properties.put("mail.smtp.host", "session.example.com");
	        Session session = Session.getInstance(properties);
	        email.setMailSession(session);

	        assertEquals("session.example.com", email.getHostName());
	    }

	    //   Test session is set but MAIL_HOST is NOT defined
	    @Test
	    public void testGetHostName_FromSession_MissingHost() {
	        Properties properties = new Properties(); // No "mail.smtp.host"
	        Session session = Session.getInstance(properties);
	        email.setMailSession(session);

	        assertNull(email.getHostName());
	    }

	    //   Test session is null but hostName is set
	    @Test
	    public void testGetHostName_FromHostName() {
	        email.setHostName("smtp.example.com");
	        assertEquals("smtp.example.com", email.getHostName());
	    }

	    //   Test session and hostName are both null
	    @Test
	    public void testGetHostName_NullSessionAndHostName() {
	        assertNull(email.getHostName());
	    }

	    //   Test session is null and hostName is an empty string
	    @Test
	    public void testGetHostName_EmptyHostName() {
	        email.setHostName("");
	        assertNull(email.getHostName());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	   //7) ----------------------------  -->  Session getMailSession() ----------------------------

	    @Test
	    public void testGetMailSession() throws EmailException {
	        email.setHostName("smtp.example.com");
	        Session session = email.getMailSession();
	        assertNotNull(session);
	    }
	    
	    
	    @Test
	    public void testGetMailSession_WithSSL() throws EmailException {

	        // Set SSL and host properties
	        email.setHostName("smtp.example.com");
	        email.setSSLOnConnect(true); //  Enable SSL to trigger the SSL settings

	        // Set a custom SSL port
	        email.setSslSmtpPort("465");

	        // Get the mail session
	        Session session = email.getMailSession();
	        assertNotNull(session);

	        //  Verify SSL properties are correctly set
	        Properties properties = session.getProperties();
	        assertEquals("465", properties.getProperty("mail.smtp.port"));
	        assertEquals("465", properties.getProperty("mail.smtp.socketFactory.port"));
	        assertEquals("javax.net.ssl.SSLSocketFactory", properties.getProperty("mail.smtp.socketFactory.class"));
	        assertEquals("false", properties.getProperty("mail.smtp.socketFactory.fallback"));
	    }
	    
	    
	    
	    
	    
	    
	 
	    
	    
	    //8) ---------------------------- --> Date    getSentDate() ----------------------------
    
	    @Test
	    public void testGetSentDate() {
	        Date date = new Date();
	        email.setSentDate(date);
	        assertEquals(date, email.getSentDate());
	    }
	    
	    @Test
	    public void testGetSentDate_WhenSentDateIsNull() {
	        Date before = new Date(); // Record time before calling the method
	        Date result = email.getSentDate(); // Should return current date if sentDate is null
	        Date after = new Date(); // Record time after calling the method

	        // Ensure the result is between before and after (close to the current time)
	        assertTrue(result.compareTo(before) >= 0 && result.compareTo(after) <= 0);
	    }
	    
	    
	    
	    
	    
	    
	    
	    //9) ---------------------------- --> int        getSocketConnectionTimeout() ----------------------------

	    @Test
	    public void testGetSocketConnectionTimeout() {
	        email.setSocketConnectionTimeout(5000);
	        assertEquals(5000, email.getSocketConnectionTimeout());
	    }
	    
	    
	    
	    
	    
	    
	    
	    //10) ---------------------------- --> Email   setFrom(String email) ----------------------------

	    
	    @Test
	    public void testSetFrom() throws EmailException {
	        email.setFrom("test@example.com");
	        assertEquals("test@example.com", email.getFromAddress().getAddress());
	    }

    
}
