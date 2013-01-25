package com.k_int.sgdrm

class CreatedMedia {

    String              recordId;
	String				name;
	String				email;
	String				encryptionKey;
	Date				creationDate = new Date();
	String				watermarkedFilePath;
	String				watermarkedRelativeFilePath;
	String				securedFilePath;
	String				securedRelativeFilePath;


    static constraints = {
		watermarkedFilePath nullable: true
		watermarkedRelativeFilePath nullable: true
		securedFilePath nullable: true
		securedRelativeFilePath nullable: true
    }
}
