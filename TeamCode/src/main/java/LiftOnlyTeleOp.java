package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/**
 * Standalone TeleOp mode for controlling only the Lift mechanism.
 * Includes the LiftController class for simplicity.
 */
@TeleOp(name = "Lift Only TeleOp", group = "TeleOp")
public class LiftOnlyTeleOp extends OpMode {

    //private LiftController liftController;
    private DcMotor leftLift = null;
    private DcMotor rightLift = null;
    private final double UP_LIFT_POWER = 1.0;
    private final double DOWN_LIFT_POWER = 1.5;
    private String direction = "Stopped";

    @Override
    public void init() {
        // Map lift motors from configuration (match these names to your Robot Configuration)
        leftLift = hardwareMap.get(DcMotorEx.class, "left_lift");
        rightLift = hardwareMap.get(DcMotorEx.class, "right_lift");

        leftLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        leftLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addLine("✅ Lift Only TeleOp Initialized");
        telemetry.addLine("Controls:");
        telemetry.addLine("⬆️ D-Pad Up = Lift Up");
        telemetry.addLine("⬇️ D-Pad Down = Lift Down");
        telemetry.addLine("⬅️ D-Pad Left = Stop Lift");
        telemetry.update();
    }

    @Override
    public void loop() {
        // --- Lift Control via Gamepad 1 D-Pad ---
        if (gamepad1.dpad_up) {
            liftUp();
        } else if (gamepad1.dpad_down) {
            liftDown();
        } else if (gamepad1.dpad_left) {
            stopLift();
        }

        // --- Automatic Stop Limits ---
        int leftCurrentPosition = leftLift.getCurrentPosition();
        int rightCurrentPosition = rightLift.getCurrentPosition();
        int MAX_LIFT_POSITION = 45775;
        int MIN_LIFT_POSITION = 400;

        // Stop if left_lift position is above maximum
        if (leftCurrentPosition >= MAX_LIFT_POSITION && direction.equals("Up")) {
            stopLift();
            telemetry.addLine("⚠️ LEFT_LIFT UP lift height reached!");
        }

        // Stop if left_lift position is below minimum
        if (leftCurrentPosition <= MIN_LIFT_POSITION && direction.equals("Down")) {
            stopLift();
            telemetry.addLine("⚠️ LEFT_LIFT BOTTOM limit reached!");
        }

        // Stop if right_lift position is above maximum
        if (rightCurrentPosition >= MAX_LIFT_POSITION && direction.equals("Up")) {
            stopLift();
            telemetry.addLine("⚠️ RIGHT_LIFT UP lift height reached!");
        }

        // Stop if right_lift position is below minimum
        if (rightCurrentPosition <= MIN_LIFT_POSITION && direction.equals("Down")) {
            stopLift();
            telemetry.addLine("⚠️ RIGHT_LIFT BOTTOM limit reached!");
        }

        // --- Telemetry Feedback ---
        telemetry.addData("Lift Direction", getDirection());
        telemetry.addData("Left Lift Pos", getLeftPosition());
        telemetry.addData("Right Lift Pos", getRightPosition());
        telemetry.update();
    }

    /** Lift up */
    private void liftUp() {
        leftLift.setPower(UP_LIFT_POWER);
        rightLift.setPower(UP_LIFT_POWER);
        direction = "Up";
    }

    /** Lift down */
    private void liftDown() {
        leftLift.setPower(-DOWN_LIFT_POWER);
        rightLift.setPower(-DOWN_LIFT_POWER);
        direction = "Down";
    }

    /** Stop the lift */
    private void stopLift() {
        leftLift.setPower(0);
        rightLift.setPower(0);
        direction = "Stopped";
    }

    /** Get direction for telemetry */
    private String getDirection() {
        return direction;
    }

    /** Encoder positions */
    private int getLeftPosition() {
        return leftLift.getCurrentPosition();
    }

    private int getRightPosition() {
        return rightLift.getCurrentPosition();
    }



}
