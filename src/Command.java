import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import javax.swing.Timer;

public class Command
implements Serializable {
	private static final long serialVersionUID = 1;
	static final int COMMAND_STARTSWITH = 1;
	static final int COMMAND_CONTAINS = 2;
	static final int COMMAND_EQUALS = 3;
	public String trigger;
	public int type;
	public String response;
	public int userLevel = 0;
	public int timerLength = 3000;
	public Timer timer;
	public boolean canRespond = true;

	public Command(String trigger, int type, String response) {
		this.trigger = trigger;
		this.type = type;
		this.response = response;
		this.timer = new Timer(this.timerLength, new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Command.this.canRespond = true;
			}
		});
	}

	public Command(String trigger, int type, String response, int userLevel) {
		this.trigger = trigger;
		this.type = type;
		this.response = response;
		this.userLevel = userLevel;
		this.timer = new Timer(this.timerLength, new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Command.this.canRespond = true;
			}
		});
	}

	public Command(String trigger, int type, String response, int userLevel, int timerLength) {
		this.trigger = trigger;
		this.type = type;
		this.response = response;
		this.userLevel = userLevel;
		this.timerLength = timerLength;
		this.timer = new Timer(timerLength, new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Command.this.canRespond = true;
			}
		});
	}

	public String getResponse(String message, int level) {
		if(canRespond){
			switch (this.type) {
			case 1: {
				if (!message.startsWith(this.trigger) || !this.canRespond || level <= this.userLevel) break;
				this.canRespond = false;
				this.timer.restart();
				return this.response;
			}
			case 2: {
				if (!message.matches(".*" + this.trigger + ".*") || !this.canRespond || level <= this.userLevel) break;
				this.canRespond = false;
				this.timer.restart();
				return this.response;
			}
			case 3: {
				if (!message.equalsIgnoreCase(this.trigger) || !this.canRespond || level <= this.userLevel) break;
				this.canRespond = false;
				this.timer.restart();
				return this.response;
			}
			default: {
				return "";
			}
		}
		}
		return "";
	}
}