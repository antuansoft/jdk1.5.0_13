/*
 * @(#)Monitor.cpp	1.2 04/07/27
 * 
 * Copyright (c) 2004 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY 
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, 
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "jni.h"
#include "jvmti.h"
#include "Monitor.hpp"

/* Implementation of the Monitor class */

Monitor::Monitor(jvmtiEnv *jvmti, JNIEnv *env, jobject object) {
    jclass klass;
    char  *signature;

    /* Clear counters */
    contends  = 0;
    waits     = 0;
    timeouts  = 0;
    
    /* Get the class name for this monitor object */
    (void)strcpy(name, "Unknown");
    klass = env->GetObjectClass(object);
    jvmti->GetClassSignature(klass, &signature, NULL);
    if ( signature != NULL ) {
	(void)strncpy(name, signature, (int)sizeof(name)-1);
        jvmti->Deallocate((unsigned char*)signature);
    }
}

Monitor::~Monitor() {
    fprintf(stdout, "Monitor %s summary: %d contends, %d waits, %d timeouts\n",
	name, contends, waits, timeouts);
}

void Monitor::contended() {
    contends++;
}

void Monitor::waited() {
    waits++;
}

void Monitor::timeout() {
    timeouts++;
}

