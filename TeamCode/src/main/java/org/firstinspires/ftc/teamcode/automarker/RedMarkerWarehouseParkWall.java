package org.firstinspires.ftc.teamcode.automarker;

// Auto Imports
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.teamcode.classes.AutonomousUtil;


@Autonomous (name= "Red Marker Warehouse Park Wall", group= "Autonomous")
public class RedMarkerWarehouseParkWall extends AutonomousUtil {
    @Override
    public void runOpMode() {
        hardwareInit();

        //AUTO START
        waitForStart();
        // Move up to the barcode to scan.
        encoderDrive(driveSpeed, 15.25, 15.25);

        encoderStrafe(strafeSpeed, "right", 4);

        // Strafe left to scan with a special speed for scan strafing
        markerStrafe(.35, "left", 3400);


        // Save where the maker is and move to the front of the shipping hub.
        // Set to three (right) if non-detected.
        int markerSquare = 3;

        // Square one (left)
        if (found2 && Math.abs(fLeft.getCurrentPosition()) >= 650) {
            markerSquare = 1;
            // Get to the center
            encoderStrafe(strafeSpeed, "left", 20);
        }
        //Square two (middle)
        else if ((found && Math.abs(fLeft.getCurrentPosition()) >= 400) || (found2 && fRight.getCurrentPosition() <= 200)) {
            markerSquare = 2;
            // Get to the center
            encoderStrafe(strafeSpeed, "left", 21);
        }
        else if (found) {
            encoderStrafe(strafeSpeed, "left", 28.25);
        }

        switch (markerSquare) {
            // Get the arm to the corresponding level and drive forwards to the shipping hub.
            case 1:
                encoderArm(1, true, 1.5);
                encoderDrive(driveSpeed, 5, 5);
                break;
            case 2:
                encoderArm(1, true, 4);
                encoderDrive(driveSpeed, 2.5, 2.5);
                break;
            default:
                encoderDrive(driveSpeed, -0.5, -0.5);
                encoderArm(1, true, 7);

        }

        // Drop the block
        leftGrabber.setPower(-1);
        rightGrabber.setPower(-1);
        sleep(2000);
        leftGrabber.setPower(0);
        rightGrabber.setPower(0);

        // Move backwards
        encoderDrive(driveSpeed,-5.5,-5.5);
        // Raise the arm
        switch (markerSquare) {
            case 1:
                encoderArm(1, true, 4.5);
                break;
            case 2:
                encoderArm(1, true, 2);
                break;
        }
        // Turn 90 degrees to the left.
        rotate(turnSpeed, "right", 90);
        // Drive up to the barrier.
        encoderDrive(driveSpeed,25,25);

        // Drive away from the barrier
        encoderDrive(driveSpeed, -1, -1);

        // Drive into the wall
        encoderStrafe(strafeSpeed, "right", 16);

        /*
        // Debugging Telemetry data for the end of the code.
        while (opModeIsActive()) {
            telemetry.addData("Found", found);
            telemetry.addData("Found2", found2);
            telemetry.addData("Square Number", markerSquare);
            telemetry.addData("Front Position", "Running at %7d :%7d", fLeft.getCurrentPosition(), fRight.getCurrentPosition());
            telemetry.addData("Back Position", "Running at %7d :%7d", bLeft.getCurrentPosition(), bRight.getCurrentPosition());
            // telemetry.addData("Rotation", angles.firstAngle);
            telemetry.update();
        }

         */

    }
}