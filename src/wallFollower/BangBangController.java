package wallFollower;

import lejos.hardware.motor.*;

public class BangBangController implements UltrasonicController {
	private final int bandCenter, bandwidth;
	private final int motorLow, motorHigh;
	private int distance;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private int distError;

	public BangBangController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, int bandCenter,
			int bandwidth, int motorLow, int motorHigh) {
		// Default Constructor
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		leftMotor.setSpeed(motorHigh); // Start robot moving forward
		rightMotor.setSpeed(motorHigh);
		leftMotor.forward();
		rightMotor.forward();
	}

	@Override
	public void processUSData(int distance) {
		this.distance = distance;
		// TODO: process a movement based on the us distance passed in
		// (BANG-BANG style)
		// Code for left side being close to the wall

		// Difference between desired distance (bandCenter) and read value
		// (distance).
		this.distError = bandCenter - distance;

		// At around bandcenter with bandwidth tolerance
		if (Math.abs(distError) <= bandwidth) {
			// Set motors for robot to go straight
			leftMotor.setSpeed(motorHigh);
			rightMotor.setSpeed(motorHigh);
			leftMotor.forward();
			rightMotor.forward();
		}
		// Robot too close to the wall
		else if (this.distance < bandCenter) {
			
			// If too close, go backwards
			if(this.distance<10){
				leftMotor.setSpeed(motorLow);
				rightMotor.setSpeed(motorLow);
				leftMotor.backward();
				rightMotor.backward();
			} 
			// Set motors for the robot to turn right (away from the wall)
			else {
				leftMotor.setSpeed(motorHigh);
				rightMotor.setSpeed(motorLow);
				leftMotor.forward();
				// Right Motor going backwards for sharper turns
				rightMotor.backward();
			}
			
		
		}
		// Robot too far from the wall
		else if (this.distance > bandCenter) {
			// Set motors for the robot to turn left (towards the wall)
			leftMotor.setSpeed(motorLow);
			rightMotor.setSpeed((motorHigh - 50));
			leftMotor.forward();
			rightMotor.forward();
		}

	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}
