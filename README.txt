This project contains a couple of demo servers whose aim is to help test TLS behavior.
The projects are:

session_breaking:

    This is a very simple jetty based server to explore how browsers react to session breaking. Do they offer the user to choose a new certificate? Do they react in particular ways to the exceptions sent when the session is broken? It turns out that they don't, but try it out yourself. (Safari did react differently up to a point)

session_renegotiation:

    This is a very simple RESTlet based server that is designed to see how browsers react to session renegotation, and also to see how what one needs to write to get this going. 
    Session renegotation is very important if we are going to have 100% ssl servers and not have to request the certificate from a client.
