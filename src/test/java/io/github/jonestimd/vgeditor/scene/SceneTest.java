// The MIT License (MIT)
//
// Copyright (c) 2018 Tim Jones
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
package io.github.jonestimd.vgeditor.scene;

import java.lang.reflect.Field;

import io.github.jonestimd.vgeditor.JavafxTest;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.EventType;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.junit.Before;

public abstract class SceneTest extends JavafxTest {
    protected Group diagram = new Group();
    protected Scene scene = new Scene(diagram);

    @Before
    public void setUpScene() throws Exception {
        Stage window = new Stage();
        window.setX(0);
        window.setY(0);
        window.setScene(scene);
    }

    @SuppressWarnings("unchecked")
    protected void setScene(Node node) throws Exception {
        Field sceneField = Node.class.getDeclaredField("scene");
        sceneField.setAccessible(true);
        ReadOnlyObjectWrapper<Scene> sceneProperty = (ReadOnlyObjectWrapper<Scene>) sceneField.get(node);
        sceneProperty.set(scene);
    }

    protected MouseEvent getMouseEvent(EventType<MouseEvent> eventType, double x, double y, boolean controlDown) {
        return new MouseEvent(diagram, diagram, eventType, x, y, x, y, MouseButton.PRIMARY, 0,
                false, controlDown, false, false, true, false, false, false, false, false, null);
    }
}
