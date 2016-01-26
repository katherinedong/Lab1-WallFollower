package wallFollower;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class PController implements UltrasonicController {

	private final int bandCenter, bandwidth;
	private final int motorStraight = 250, FILTER_OUT = 20;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private int distance;
	private int filterControl;
	private int distError;
	private double correction;

	public PController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, int bandCenter,
			int bandwidth) {
		// Default Constructor
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		leftMotor.setSpeed(motorStraight); // Initalize motor rolling forward
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		filterControl = 0;
		this.distError = 0;
		this.correction = 1.0;
	}

	@Override
	public void processUSData(int distance) {

		// rudimentary filter - toss out invalid samples corresponding to null
		// signal.
		// (n.b. this was not included in the Bang-bang controller, but easily
		// could have).
		//
		if (distance == 255 && filterControl < FILTER_OUT) {
			// bad value, do not set the distance var, however do increment the
			// filter value
			filterControl++;
		} else if (distance == 255) {
			// true 255, therefore set distance to 255
			this.distance = distance;
		} else {
			// distance went below 255, therefore reset everything.
			filterControl = 0;
			this.distance = distance;
		}

		// TODO: process a movement based on the us distance passed in (P style)

		this.distance = distance;
		// TODO: process a movement based on the us distance passed in
		// (BANG-BANG style)
		// Sensor on left, wall on left
		this.distError = bandCenter - distance;

		// At around bandcenter with bandwidth tolerance
		if (Math.abs(distError) <= bandwidth) {
			// Set motors for robot to go straight
			leftMotor.setSpeed(motorStraight);
			rightMotor.setSpeed(motorStraight);
			leftMotor.forward();
			rightMotor.forward();
		}
		// Robot too close to the wall
		else if (this.distance < bandCenter) {
			// Calculate the correction coefficient
			// The closer the robot is to the wall, the higher the coefficient
			// is.
			correction = 1.0 + distError / 40.0;
			// Set motors for the robot to turn right (away from the wall)
			// in proportion to the correction value
			leftMotor.setSpeed((int) (correction * motorStraight));
			rightMotor.setSpeed((int) (correction * 30));
			leftMotor.forward();
			// Right motor set to turn backwards for sharper turns
			rightMotor.backward();
		}
		// Robot too far from the wall
		else if (this.distance > bandCenter) {
			// Calculate correction coefficient, the furthest away, the higher
			// the value.
			correction = 1.0 + distance / 255.0;
			// Set a maximum for the coefficient (since the US sensor returns
			// abnormally high values (~20,000).
			if (correction > 2.0) {
				correction = 1.5;
			}
			leftMotor.setSpeed((int) (100 - 5 * correction));
			rightMotor.setSpeed((int) (correction * motorStraight) - 50);

			leftMotor.forward();
			rightMotor.forward();
		}

	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}

}
