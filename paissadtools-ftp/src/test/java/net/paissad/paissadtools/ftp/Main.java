package net.paissad.paissadtools.ftp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        String user = "paissad";
        String password = "missmiss";
        String host = "localhost";

        final FtpSettings ftpSettings = new FtpSettings(user, password, host);

        // String dir = "/subdir/another_dir/foo/bar";

        List<File> files = new ArrayList<File>();
        files.add(new File("pom.xml"));
        files.add(new File("stuffs.txt"));

        final FtpService ftpService = new FtpService();
        final Ftp ftp = ftpService.createEntry(ftpSettings);

        if (!ftp.connect()) {
            throw new IllegalStateException("Unable to connect to the FTP server.");
        }

        ftp.setLogReceiver(System.out);

        // ftp.mkdirs("/dir_created_from_ftpservice_java");
        // ftp.mkdirs("/subdir/another_dir/foo/bar/dir1");
        // ftp.getResource("Downloads", new File("/tmp/aaaaa"), true);
        // ftp.getResource(".vimrc", new FileOutputStream("/tmp/vimrc"));
        // ftp.getResource("symlink", new FileOutputStream("/tmp/totosymlink"));
        // ftp.getResource("testftpdir", new File("/tmp/aaaaa"), true);

        // ftp.putResource(new File("/tmp/fakerepo"), "tmp_ftp", true);

        // ftp.deleteResource("symlink");
        // ftp.deleteResource("lol.txt");

        ftp.deleteResource("tmp/toto");
        // ftp.deleteResource("tmp_ftp");

        if (!ftp.disconnect()) {
            throw new IllegalStateException("Unable to disconnect from the FTP server.");
        }

    }

}
