/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.guvnor.projecteditor.client.forms;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.project.model.POM;
import org.kie.guvnor.project.service.POMService;
import org.kie.guvnor.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.kie.guvnor.project.service.KModuleService;
import org.uberfire.backend.vfs.Path;

import javax.inject.Inject;

public class POMEditorPanel
        implements IsWidget {

    private final POMEditorPanelView view;
    private Path path;
    private POM model;
    private final Caller<POMService> pomServiceCaller;

    @Inject
    public POMEditorPanel(Caller<POMService> pomServiceCaller,
                          POMEditorPanelView view) {
        this.pomServiceCaller = pomServiceCaller;
        this.view = view;

    }

    public void init(Path path) {
        this.path = path;
        pomServiceCaller.call(
                new RemoteCallback<POM>() {
                    @Override
                    public void callback(final POM model) {
                        POMEditorPanel.this.model = model;

                        view.setGAV(model.getGav());

                        view.addArtifactIdChangeHandler(new ArtifactIdChangeHandler() {
                            @Override
                            public void onChange(String newArtifactId) {
                                setTitle(newArtifactId);
                            }
                        });

                        setTitle(model.getGav().getArtifactId());

                        view.setDependencies(POMEditorPanel.this.model.getDependencies());
                    }
                }
        ).loadPOM(path);
    }

    private void setTitle(String titleText) {
        if (titleText == null || titleText.isEmpty()) {
            view.setTitleText(ProjectEditorConstants.INSTANCE.ProjectModel());
        } else {
            view.setTitleText(titleText);
        }
    }

    public void save(final Command callback) {
        pomServiceCaller.call(
                new RemoteCallback<Path>() {
                    @Override
                    public void callback(Path path) {
                        callback.execute();
                        view.showSaveSuccessful("pom.xml");
                    }
                }
        ).savePOM(path, model);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public IsWidget getTitle() {
        return view.getTitleWidget();
    }
}