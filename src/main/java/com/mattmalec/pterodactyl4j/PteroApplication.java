package com.mattmalec.pterodactyl4j;

import com.mattmalec.pterodactyl4j.application.entities.Node;
import com.mattmalec.pterodactyl4j.application.entities.User;
import com.mattmalec.pterodactyl4j.application.managers.UserManager;

import java.util.List;

public interface PteroApplication {

	PteroAction<List<User>> retrieveUsers();
	PteroAction<User> retrieveUserById(String id);
	PteroAction<User> retrieveUserById(long id);
	PteroAction<List<User>> retrieveUsersByUsername(String name, boolean caseSensetive);
	UserManager getUserManager();

	PteroAction<List<Node>> retrieveNodes();
	PteroAction<Node> retrieveNodeById(String id);
	PteroAction<Node> retrieveNodeById(long id);
	PteroAction<List<Node>> retrieveNodesByName(String name, boolean caseSensetive);


}
