/**
 * Copyright (C) 2015 VanirAOSP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package co.vanir.indecentxposure;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class IndecentTester extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indecent_tester);

        ((Button) findViewById(R.id.checknow)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IndecentXposure.getInstance().start(getApplication());
            }
        });
        ((Button)findViewById(R.id.trigger)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IndecentXposure.notify(getApplication(), "Forced by button");
            }
        });
        ((Button)findViewById(R.id.suppress)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IndecentXposure.cancel(getApplication());
            }
        });
    }
}
