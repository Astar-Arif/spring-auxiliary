package com.astar.spring.library;

public class Views {
    public interface Public {
    }

    public interface Sensitive extends Public {
    }

    public interface Secret extends Sensitive {
    }

    public interface Confidential extends Secret {
    }

    public interface Restricted extends Confidential {
    }

    public interface Classified extends Restricted {
    }

    public interface TopSecret extends Classified {
    }

    public interface Proprietary extends TopSecret {
    }

    public interface Critical extends Proprietary {
    }

    public interface Privileged extends Critical {
    }

    public interface HighlySensitive extends Privileged {
    }
}
