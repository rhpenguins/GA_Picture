  const Http = new XMLHttpRequest();
  const dataURL = '/yuki/data';
  const configURL = '/yuki/config';
  var jsonData;
  var background;


  getData(configURL, (result) => {
    var canvas = document.getElementById('canvas');
    jsonConfig = result;
    canvas.width = jsonConfig.width;
    canvas.height = jsonConfig.height;
    background = jsonConfig.backgroundColor;
    requestData();
  });


  function loop() {
    var canvas = document.getElementById('canvas');
    var table = document.getElementById('table');
    table.rows[0].cells[1].innerHTML = jsonData.generation;
    table.rows[1].cells[1].innerHTML = jsonData.fitness;
    if (canvas.getContext) {
      var ctx = canvas.getContext('2d');
      ctx.fillStyle = background;
      ctx.fillRect(0, 0, canvas.width, canvas.height);
      for (var circle of jsonData.circles) {
        ctx.fillStyle = rbgaToString(circle.color.r*255, circle.color.g*255, circle.color.b*255, circle.color.a);
        ctx.beginPath();
        ctx.arc(circle.x, circle.y, circle.radius, 0, Math.PI * 2);
        ctx.fill();
      }
    }
    requestData();
  }

  function requestData() {
    getData(dataURL, (result) => {
      requestAnimationFrame(loop);
      jsonData = result;
    });
  }

  function getData(URL, ready) {
    Http.open("GET", URL);
    Http.send();
    Http.onreadystatechange = () => {
      if (Http.readyState === 4 && Http.status === 200) {
        ready(JSON.parse(Http.responseText));
      }
    }
  }
  var button = document.getElementById('SVG');
  button.style.visibility = "visible";
  button.addEventListener('click', function (e) {
    var canvas = document.getElementById('canvas');
    var NS = "http://www.w3.org/2000/svg";
    var svg = document.createElementNS(NS, "svg");
    svg.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", NS);
    svg.setAttributeNS(null, "width", canvas.width);
    svg.setAttributeNS(null, "height", canvas.height);
    var backgroundRect = document.createElementNS(NS, "rect");
    backgroundRect.width.baseVal.value = canvas.width;
    backgroundRect.height.baseVal.value = canvas.height;
    backgroundRect.style.fill = background;
    svg.appendChild(backgroundRect);
    for (var circle of jsonData.circles) {
      var circ = document.createElementNS(NS, "circle");
      circ.style.fill = rbgaToString(circle.color.r, circle.color.g, circle.color.b, circle.color.a);
      circ.cx.baseVal.value = circle.x;
      circ.cy.baseVal.value = circle.y;
      circ.r.baseVal.value = circle.radius;
      svg.appendChild(circ);
    }
    button.href = 'data:text/plain;charset=utf-8,' + encodeURIComponent(svg.outerHTML);
    button.download = "picture.svg";
    svg.remove();
  });

  function rbgaToString(r, g, b, a) {
    return "rgba(" + r + "," + g + "," + b + ", " + a + ")";
  }
