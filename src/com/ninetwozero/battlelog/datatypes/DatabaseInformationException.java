package com.ninetwozero.battlelog.datatypes;

import com.ninetwozero.battlelog.R;

public class DatabaseInformationException extends Exception {

	private static final long serialVersionUID = -4284315651483396603L;

	public DatabaseInformationException() { super( "DatabaseInformationException" ); }

	public DatabaseInformationException( String detailMessage ) { super( ( detailMessage == null ) ? "No error message found" : detailMessage ); }

}
