package frc.util;

import edu.wpi.first.math.geometry.Pose3d;

public class PoseAndTimestamp {
    public Pose3d pose;
    public double timestamp;
    public boolean isNew;

    public PoseAndTimestamp(Pose3d pose, double timestamp, boolean isNew) {
        this.pose = pose;
        this.timestamp = timestamp;
        this.isNew = isNew;
    }

    public PoseAndTimestamp(Pose3d pose, double timestamp) {
        this.pose = pose;
        this.timestamp = timestamp;
        this.isNew = true;
    }

    public Pose3d getPose()             { return pose; }
    public double getTimestamp()        { return timestamp; }
    public boolean isNew()              { return isNew; }
}

