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

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBar;

public class Nodes {
    public static double boundingArea(Node node) {
        Bounds bounds = node.getBoundsInLocal();
        return bounds.getWidth()*bounds.getHeight();
    }

    public static void visit(Parent root, Predicate<Node> predicate, Consumer<Node> visitor) {
        root.getChildrenUnmodifiable().forEach(node -> {
            if (predicate.test(node)) visitor.accept(node);
            else if (node instanceof Parent) visit((Parent) node, predicate, visitor);
        });
    }

    public static <T extends Node> void visit(Parent root, Class<T> type, Consumer<T> visitor) {
        visit(root, type::isInstance, (node) -> visitor.accept(type.cast(node)));
    }

    public static List<Node> findNodes(Parent root, Predicate<Node> filter) {
        List<Node> matches = new ArrayList<>();
        Stack<Parent> stack = new Stack<>();
        stack.add(root);
        while (!stack.isEmpty()) {
            Parent parent = stack.pop();
            for (Node node : parent.getChildrenUnmodifiable()) {
                if (node instanceof Parent) {
                    if (filter.test(node)) stack.push((Parent) node);
                }
                else if (filter.test(node)) matches.add(node);
            }
        }
        return matches;
    }

    public static <T extends Node> T findById(Parent root, String id, Class<T> type) {
        return findFirstById(root, id, type).orElseThrow(NoSuchElementException::new);
    }

    public static <T extends Node> Optional<T> findFirstById(Parent root, String id, Class<T> type) {
        return findFirstById(id, type, root.getChildrenUnmodifiable());
    }

    private static <T extends Node> Optional<T> findFirstById(String id, Class<T> type, ObservableList<Node> children) {
        for (Node node : children) {
            if (type.isInstance(node) && id.equals(node.getId())) return Optional.of(type.cast(node));
            if (node instanceof ButtonBar) {
                Optional<T> child = findFirstById(id, type, ((ButtonBar) node).getButtons());
                if (child.isPresent()) return child;
            }
            else if (node instanceof Parent) {
                Optional<T> child = findFirstById(id, type, ((Parent) node).getChildrenUnmodifiable());
                if (child.isPresent()) return child;
            }
        }
        return Optional.empty();
    }
}
