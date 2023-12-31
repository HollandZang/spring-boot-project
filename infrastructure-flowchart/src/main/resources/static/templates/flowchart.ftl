<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>flowchart</title>
</head>
<body style="background-color: #2d313a">
<script src="../../js/go.js"></script>
<div id="allSampleContent" class="p-4 w-full">
    <script id="code">
        var data = ${data}
        var id = data.id ?? null
        var req_url_info = '${req_url_info}'
        var req_url_saveOrUpdate = '${req_url_saveOrUpdate}'
        var flowNodes = ${flowNodes}

        function init() {
            document.getElementById("mySavedModel").value = JSON.stringify(data, null, 2);

            if (window.goSamples) goSamples();  // init for these samples -- you don't need to call this

            // Since 2.2 you can also author concise templates with method chaining instead of GraphObject.make
            // For details, see https://gojs.net/latest/intro/buildingObjects.html
            const $ = go.GraphObject.make;  // for conciseness in defining templates

            myDiagram =
                new go.Diagram("myDiagramDiv",  // must name or refer to the DIV HTML element
                    {
                        "LinkDrawn": showLinkLabel,  // this DiagramEvent listener is defined below
                        "LinkRelinked": showLinkLabel,
                        "undoManager.isEnabled": true  // enable undo & redo
                    });

            // when the document is modified, add a "*" to the title and enable the "Save" button
            myDiagram.addDiagramListener("Modified", e => {
                var button = document.getElementById("SaveButton");
                if (button) button.disabled = !myDiagram.isModified;
                var idx = document.title.indexOf("*");
                if (myDiagram.isModified) {
                    if (idx < 0) document.title += "*";
                } else {
                    if (idx >= 0) document.title = document.title.slice(0, idx);
                }
            });

            // helper definitions for flowNode templates
            function nodeStyle() {
                return [
                    // The Node.location comes from the "loc" property of the flowNode data,
                    // converted by the Point.parse static method.
                    // If the Node.location is changed, it updates the "loc" property of the flowNode data,
                    // converting back using the Point.stringify static method.
                    new go.Binding("location", "loc", go.Point.parse).makeTwoWay(go.Point.stringify),
                    {
                        // the Node.location is at the center of each flowNode
                        locationSpot: go.Spot.Center
                    }
                ];
            }

            // Define a function for creating a "port" that is normally transparent.
            // The "name" is used as the GraphObject.portId,
            // the "align" is used to determine where to position the port relative to the body of the flowNode,
            // the "spot" is used to control how links connect with the port and whether the port
            // stretches along the side of the flowNode,
            // and the boolean "output" and "input" arguments control whether the user can draw links from or to the port.
            function makePort(name, align, spot, output, input) {
                var horizontal = align.equals(go.Spot.Top) || align.equals(go.Spot.Bottom);
                // the port is basically just a transparent rectangle that stretches along the side of the flowNode,
                // and becomes colored when the mouse passes over it
                return $(go.Shape,
                    {
                        fill: "transparent",  // changed to a color in the mouseEnter event handler
                        strokeWidth: 0,  // no stroke
                        width: horizontal ? NaN : 8,  // if not stretching horizontally, just 8 wide
                        height: !horizontal ? NaN : 8,  // if not stretching vertically, just 8 tall
                        alignment: align,  // align the port on the main Shape
                        stretch: (horizontal ? go.GraphObject.Horizontal : go.GraphObject.Vertical),
                        portId: name,  // declare this object to be a "port"
                        fromSpot: spot,  // declare where links may connect at this port
                        fromLinkable: output,  // declare whether the user may draw links from here
                        toSpot: spot,  // declare where links may connect at this port
                        toLinkable: input,  // declare whether the user may draw links to here
                        cursor: "pointer",  // show a different cursor to indicate potential link point
                        mouseEnter: (e, port) => {  // the PORT argument will be this Shape
                            if (!e.diagram.isReadOnly) port.fill = "rgba(255,0,255,0.5)";
                        },
                        mouseLeave: (e, port) => port.fill = "transparent"
                    });
            }

            function textStyle() {
                return {
                    font: "bold 11pt Lato, Helvetica, Arial, sans-serif",
                    stroke: "#F8F8F8"
                }
            }

            // define the Node templates for regular nodes

            myDiagram.nodeTemplateMap.add("",  // the default category
                $(go.Node, "Table", nodeStyle(),
                    // the main object is a Panel that surrounds a TextBlock with a rectangular Shape
                    $(go.Panel, "Auto",
                        $(go.Shape, "Rectangle",
                            {fill: "#282c34", stroke: "#00A9C9", strokeWidth: 3.5},
                            new go.Binding("figure", "figure")),
                        $(go.TextBlock, textStyle(),
                            {
                                margin: 8,
                                maxSize: new go.Size(160, NaN),
                                wrap: go.TextBlock.WrapFit,
                                editable: true
                            },
                            new go.Binding("text").makeTwoWay())
                    ),
                    // four named ports, one on each side:
                    makePort("T", go.Spot.Top, go.Spot.TopSide, false, true),
                    makePort("L", go.Spot.Left, go.Spot.LeftSide, true, true),
                    makePort("R", go.Spot.Right, go.Spot.RightSide, true, true),
                    makePort("B", go.Spot.Bottom, go.Spot.BottomSide, true, false)
                ));

            myDiagram.nodeTemplateMap.add("Conditional",
                $(go.Node, "Table", nodeStyle(),
                    // the main object is a Panel that surrounds a TextBlock with a rectangular Shape
                    $(go.Panel, "Auto",
                        $(go.Shape, "Diamond",
                            {fill: "#282c34", stroke: "#00A9C9", strokeWidth: 3.5},
                            new go.Binding("figure", "figure")),
                        $(go.TextBlock, textStyle(),
                            {
                                margin: 8,
                                maxSize: new go.Size(160, NaN),
                                wrap: go.TextBlock.WrapFit,
                                editable: true
                            },
                            new go.Binding("text").makeTwoWay())
                    ),
                    // four named ports, one on each side:
                    makePort("T", go.Spot.Top, go.Spot.Top, false, true),
                    makePort("L", go.Spot.Left, go.Spot.Left, true, true),
                    makePort("R", go.Spot.Right, go.Spot.Right, true, true),
                    makePort("B", go.Spot.Bottom, go.Spot.Bottom, true, false)
                ));

            myDiagram.nodeTemplateMap.add("Start",
                $(go.Node, "Table", nodeStyle(),
                    $(go.Panel, "Spot",
                        $(go.Shape, "Circle",
                            {desiredSize: new go.Size(70, 70), fill: "#282c34", stroke: "#09d3ac", strokeWidth: 3.5}),
                        $(go.TextBlock, "Start", textStyle(),
                            new go.Binding("text"))
                    ),
                    // three named ports, one on each side except the top, all output only:
                    makePort("L", go.Spot.Left, go.Spot.Left, true, false),
                    makePort("R", go.Spot.Right, go.Spot.Right, true, false),
                    makePort("B", go.Spot.Bottom, go.Spot.Bottom, true, false)
                ));

            myDiagram.nodeTemplateMap.add("End",
                $(go.Node, "Table", nodeStyle(),
                    $(go.Panel, "Spot",
                        $(go.Shape, "Circle",
                            {desiredSize: new go.Size(60, 60), fill: "#282c34", stroke: "#DC3C00", strokeWidth: 3.5}),
                        $(go.TextBlock, "End", textStyle(),
                            new go.Binding("text"))
                    ),
                    // three named ports, one on each side except the bottom, all input only:
                    makePort("T", go.Spot.Top, go.Spot.Top, false, true),
                    makePort("L", go.Spot.Left, go.Spot.Left, false, true),
                    makePort("R", go.Spot.Right, go.Spot.Right, false, true)
                ));

            // taken from https://unpkg.com/gojs@2.3.12/extensions/Figures.js:
            go.Shape.defineFigureGenerator("File", (shape, w, h) => {
                var geo = new go.Geometry();
                var fig = new go.PathFigure(0, 0, true); // starting point
                geo.add(fig);
                fig.add(new go.PathSegment(go.PathSegment.Line, .75 * w, 0));
                fig.add(new go.PathSegment(go.PathSegment.Line, w, .25 * h));
                fig.add(new go.PathSegment(go.PathSegment.Line, w, h));
                fig.add(new go.PathSegment(go.PathSegment.Line, 0, h).close());
                var fig2 = new go.PathFigure(.75 * w, 0, false);
                geo.add(fig2);
                // The Fold
                fig2.add(new go.PathSegment(go.PathSegment.Line, .75 * w, .25 * h));
                fig2.add(new go.PathSegment(go.PathSegment.Line, w, .25 * h));
                geo.spot1 = new go.Spot(0, .25);
                geo.spot2 = go.Spot.BottomRight;
                return geo;
            });

            myDiagram.nodeTemplateMap.add("Comment",
                $(go.Node, "Auto", nodeStyle(),
                    $(go.Shape, "File",
                        {fill: "#282c34", stroke: "#DEE0A3", strokeWidth: 3}),
                    $(go.TextBlock, textStyle(),
                        {
                            margin: 8,
                            maxSize: new go.Size(200, NaN),
                            wrap: go.TextBlock.WrapFit,
                            textAlign: "center",
                            editable: true
                        },
                        new go.Binding("text").makeTwoWay())
                    // no ports, because no links are allowed to connect with a comment
                ));


            // replace the default Link template in the linkTemplateMap
            myDiagram.linkTemplate =
                $(go.Link,  // the whole link panel
                    {
                        routing: go.Link.AvoidsNodes,
                        curve: go.Link.JumpOver,
                        corner: 5, toShortLength: 4,
                        relinkableFrom: true,
                        relinkableTo: true,
                        reshapable: true,
                        resegmentable: true,
                        // mouse-overs subtly highlight links:
                        mouseEnter: (e, link) => link.findObject("HIGHLIGHT").stroke = "rgba(30,144,255,0.2)",
                        mouseLeave: (e, link) => link.findObject("HIGHLIGHT").stroke = "transparent",
                        selectionAdorned: false,
                    },
                    new go.Binding("points").makeTwoWay(),
                    $(go.Shape,  // the highlight shape, normally transparent
                        {isPanelMain: true, strokeWidth: 8, stroke: "transparent", name: "HIGHLIGHT"}),
                    $(go.Shape,  // the link path shape
                        {isPanelMain: true, stroke: "gray", strokeWidth: 2},
                        new go.Binding("stroke", "isSelected", sel => sel ? "dodgerblue" : "gray").ofObject()),
                    $(go.Shape,  // the arrowhead
                        {toArrow: "standard", strokeWidth: 0, fill: "gray"}),
                    $(go.Panel, "Auto",  // the link label, normally not visible
                        {visible: false, name: "LABEL", segmentIndex: 2, segmentFraction: 0.5},
                        new go.Binding("visible", "visible").makeTwoWay(),
                        $(go.Shape, "RoundedRectangle",  // the label shape
                            {fill: "#F8F8F8", strokeWidth: 0}),
                        $(go.TextBlock, "Yes",  // the label
                            {
                                textAlign: "center",
                                font: "10pt helvetica, arial, sans-serif",
                                stroke: "#333333",
                                editable: true
                            },
                            new go.Binding("text").makeTwoWay())
                    )
                );

            // Make link labels visible if coming out of a "conditional" flowNode.
            // This listener is called by the "LinkDrawn" and "LinkRelinked" DiagramEvents.
            function showLinkLabel(e) {
                var label = e.subject.findObject("LABEL");
                if (label !== null) label.visible = (e.subject.fromNode.data.category === "Conditional");
            }

            // temporary links used by LinkingTool and RelinkingTool are also orthogonal:
            myDiagram.toolManager.linkingTool.temporaryLink.routing = go.Link.Orthogonal;
            myDiagram.toolManager.relinkingTool.temporaryLink.routing = go.Link.Orthogonal;

            load();  // load an initial diagram from some JSON text

            // specify the contents of the Palette
            const nodes = [
                {category: "Start", text: "开始"},
                {category: "Step", text: "步骤"},
                {category: "Conditional", text: "分支"},
                {category: "End", text: "结束"},
                {category: "Comment", text: "备注"}
            ]
            for (let flowNode of flowNodes) {
                nodes.push({category: "Step", text: flowNode})
            }

            // initialize the Palette that is on the left side of the page
            myPalette =
                new go.Palette("myPaletteDiv",  // must name or refer to the DIV HTML element
                    {
                        // Instead of the default animation, use a custom fade-down
                        "animationManager.initialAnimationStyle": go.AnimationManager.None,
                        "InitialAnimationStarting": animateFadeDown, // Instead, animate with this function

                        nodeTemplateMap: myDiagram.nodeTemplateMap,  // share the templates used by myDiagram

                        model: new go.GraphLinksModel(nodes)
                    });

            // This is a re-implementation of the default animation, except it fades in from downwards, instead of upwards.
            function animateFadeDown(e) {
                var diagram = e.diagram;
                var animation = new go.Animation();
                animation.isViewportUnconstrained = true; // So Diagram positioning rules let the animation start off-screen
                animation.easing = go.Animation.EaseOutExpo;
                animation.duration = 900;
                // Fade "down", in other words, fade in from above
                animation.add(diagram, 'position', diagram.position.copy().offset(0, 200), diagram.position);
                animation.add(diagram, 'opacity', 0, 1);
                animation.start();
            }

        } // end init

        // Show the diagram's model in JSON format that the user may edit
        function save() {
            const s = myDiagram.model.toJson()
            data = JSON.parse(s);
            data.id = id;
            document.getElementById("mySavedModel").value = JSON.stringify(data, null, 2);
            myDiagram.isModified = false;

            // 持久化
            fetch(req_url_saveOrUpdate, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            })
                .then(response => response.json())
                .then(response => {
                    const s = JSON.stringify(response, null, 2);
                    document.getElementById("mySavedModel").value = s;
                    data = response;
                    id = data.id ?? null;
                    myDiagram.model = go.Model.fromJson(s);
                })
                .catch(error => console.error('Error:', error));
        }

        function load() {
            const s = document.getElementById("mySavedModel").value
            data = JSON.parse(s)
            id = data.id ?? null;
            myDiagram.model = go.Model.fromJson(s);
        }

        // print the diagram by opening a new window holding svg images of the diagram contents for each page
        function printDiagram() {
            var svgWindow = window.open();
            if (!svgWindow) return;  // failure to open a new Window
            var printSize = new go.Size(700, 960);
            var bnds = myDiagram.documentBounds;
            var x = bnds.x;
            var y = bnds.y;
            while (y < bnds.bottom) {
                while (x < bnds.right) {
                    var svg = myDiagram.makeSvg({scale: 1.0, position: new go.Point(x, y), size: printSize});
                    svgWindow.document.body.appendChild(svg);
                    x += printSize.width;
                }
                x = bnds.x;
                y += printSize.height;
            }
            // 调打印机
            // setTimeout(() => svgWindow.print(), 1);
            // todo 支持导出各种格式
        }

        window.addEventListener('DOMContentLoaded', init);

        document.addEventListener('keydown', function (event) {
            if ((event.ctrlKey || event.metaKey) && event.key === 's') {
                event.preventDefault(); // 阻止默认行为
                save();
            }
        });
    </script>

    <div style="width: 100%; display: flex; justify-content: space-between">
        <div id="myPaletteDiv"
             style="width: 110px; margin-right: 2px; background-color: rgb(40, 44, 52); --darkreader-inline-bgcolor: #464749; position: relative; -webkit-tap-highlight-color: rgba(255, 255, 255, 0);"
             data-darkreader-inline-bgcolor="">
            <canvas tabindex="0" width="110" height="750"
                    style="position: absolute; top: 0px; left: 0px; z-index: 2; user-select: none; touch-action: none; width: 100px; height: 750px;"></canvas>
            <div style="position: absolute; overflow: auto; width: 100px; height: 750px; z-index: 1;">
                <div style="position: absolute; width: 1px; height: 1px;"></div>
            </div>
        </div>
        <div id="myDiagramDiv"
             style="flex-grow: 1; height: 750px; background-color: rgb(40, 44, 52); --darkreader-inline-bgcolor: #464749; position: relative; -webkit-tap-highlight-color: rgba(255, 255, 255, 0); cursor: auto;"
             data-darkreader-inline-bgcolor="">
            <canvas tabindex="0" width="911" height="750"
                    style="position: absolute; top: 0px; left: 0px; z-index: 2; user-select: none; touch-action: none; width: 911px; height: 750px; cursor: auto;"></canvas>
            <div style="position: absolute; overflow: auto; width: 911px; height: 750px; z-index: 1;">
                <div style="position: absolute; width: 1px; height: 1px;"></div>
            </div>
        </div>
    </div>
    <button id="SaveButton" onclick="save()">保存数据</button>
    <button onclick="load()">加载数据</button>

    <!-- 保存和加载都通过这个文本区处理的 -->
    <textarea id="mySavedModel" style="width:100%;height:300px;color: white;background-color: #282c34"></textarea>
    <button onclick="printDiagram()">另存为</button>
</div>
</body>
</html>