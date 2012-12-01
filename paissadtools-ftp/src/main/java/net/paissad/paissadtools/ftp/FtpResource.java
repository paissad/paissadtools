package net.paissad.paissadtools.ftp;

import java.io.Serializable;
import java.util.Calendar;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FtpResource implements Serializable {

    private static final long serialVersionUID   = 1L;

    private static final int  FILE_TYPE          = 0;

    private static final int  DIRECTORY_TYPE     = 1;

    private static final int  SYMBOLIC_LINK_TYPE = 2;

    private static final int  UNKNOWN_TYPE       = 3;

    private static final int  USER_ACCESS        = 0;

    private static final int  GROUP_ACCESS       = 1;

    private static final int  WORLD_ACCESS       = 2;

    private static final int  READ_PERMISSION    = 0;

    private static final int  WRITE_PERMISSION   = 1;

    private static final int  EXECUTE_PERMISSION = 2;

    private int               type, hardLinkCount;
    private long              size;
    private String            rawListing, user, group, name, link;
    private Calendar          timestamp;
    // e.g. _permissions[USER_ACCESS][READ_PERMISSION]
    private boolean[]         permissions[];

    public FtpResource() {

    }

}
