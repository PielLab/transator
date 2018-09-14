FROM tomcat:7-jre8
MAINTAINER Pablo Moreno <pmoreno@ebi.ac.uk>

RUN mkdir /python-pks
RUN mkdir -p /java-code/PKSPredictor
RUN mkdir /java-runner
RUN mkdir /NRPSPredictor2

WORKDIR /java-code/PKSPredictor

ADD ./PKSPredictor-Core /python-pks/PKSPredictor/
ADD ./PKSPredictor /java-code/PKSPredictor/
ADD ./cladification /cladification/
ADD ./NRPSPredictor2 /NRPSPredictor2/

RUN apt-get update && apt-get install -y --no-install-recommends git maven libc6-i386 hmmer emboss \
    python-biopython unzip && \
    mvn install -DskipTests && \
    cp /java-code/PKSPredictor/PKSPredictorWeb/target/PKSPredictorWeb.war /usr/local/tomcat/webapps/trans-AT-PKS.war && \
    cp /java-code/PKSPredictor/PKSPredictorREST/target/PKSPredictorREST.war /usr/local/tomcat/webapps/trans-AT-PKS#rest.war && \
    cp /java-code/PKSPredictor/PKSPredictorRunner/target/PKSPredictorRunner-1.1-SNAPSHOT.jar /java-runner/PKSPredictorRunner.jar && \
    cp /java-code/PKSPredictor/Common/target/Common-1.1-SNAPSHOT.jar /java-runner/Common-1.0-SNAPSHOT.jar && \
    rm -rf /java-code /root/.m2 && \
    apt-get purge -y maven git unzip && \
    apt-get autoremove -y && apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

RUN rm -rf /usr/local/tomcat/webapps/ROOT

WORKDIR /

RUN mkdir -p /usr/local/tomcat/conf/Catalina/localhost
ADD tomcat-setup/PKSPredictorREST.xml /usr/local/tomcat/conf/Catalina/localhost/PKSPredictorREST.xml
ADD tomcat-setup/PKSPredictorWeb.xml /usr/local/tomcat/conf/Catalina/localhost/PKSPredictorWeb.xml

# we need to set the preferences automatically
RUN ["java", "-cp", "/java-runner/PKSPredictorRunner.jar:/java-runner/Common-1.0-SNAPSHOT.jar", "exec.PreferenceSetter", "PythonPath:;HMMERPath:/usr/bin;FuzzProPath:/usr/bin;ScriptPath:/python-pks/PKSPredictor/src/;HMMERModelPath:/cladification/ks_hmmer_models/PKSAllPlusClades_ConsSignal100.hmm;UseCluster:;TmpPath:/tmp;HMMEROtherModelsPath:/cladification/other_domains/otherModels;NRPS2Path:/NRPSPredictor2;MonomerMolsPath:/cladification/pks_mol_files/;AminoAcidsMolsPath:/cladification/aa_mol_files/"]
RUN ["java","-cp","/java-runner/Common-1.0-SNAPSHOT.jar","encrypt.Encrypter","alph4num3r1c"]

# Expose port 80 to the host
EXPOSE 8080 

