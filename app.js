let hist=JSON.parse(localStorage.getItem('bacboHist')||'[]');
let deferredPrompt=null;
const $=id=>document.getElementById(id);

function add(x){hist.push(x);save();render()}
function undo(){hist.pop();save();render()}
function clearAll(){if(confirm('Limpar todo o histórico?')){hist=[];save();render()}}
function save(){localStorage.setItem('bacboHist',JSON.stringify(hist))}
function render(){
 $('history').innerHTML='';
 let p=0,b=0,e=0;
 hist.forEach(x=>{
  if(x==='P')p++; else if(x==='B')b++; else e++;
  const d=document.createElement('div'); d.className='ball '+x.toLowerCase(); d.textContent=x;
  $('history').appendChild(d);
 });
 $('stats').textContent=`P: ${p}   B: ${b}   E: ${e}`;
 if(hist.length<8){
  $('signal').textContent='SEM SINAL';
  $('confidence').textContent='Confiança estatística: —';
  $('status').textContent='Introduza pelo menos 8 resultados';
  return;
 }
 const n=Math.min(12,hist.length);
 const recent=hist.slice(-n);
 const cp=recent.filter(x=>x==='P').length, cb=recent.filter(x=>x==='B').length;
 let sig='SEM SINAL', conf=50;
 if(cp>cb+2){sig='SINAL: B';conf=Math.min(72,50+(cp-cb)*5)}
 else if(cb>cp+2){sig='SINAL: P';conf=Math.min(72,50+(cb-cp)*5)}
 else if(hist.at(-1)==='P'){sig='SINAL: B';conf=52}
 else if(hist.at(-1)==='B'){sig='SINAL: P';conf=52}
 $('signal').textContent=sig;
 $('confidence').textContent=`Confiança estatística estimada: ${conf}% (não garantida)`;
 $('status').textContent=`Histórico atualizado • ${hist.length} resultados`;
}
window.addEventListener('beforeinstallprompt',e=>{e.preventDefault();deferredPrompt=e;$('install').hidden=false});
$('install').onclick=async()=>{if(deferredPrompt){deferredPrompt.prompt();await deferredPrompt.userChoice;deferredPrompt=null;$('install').hidden=true}};
if('serviceWorker' in navigator) navigator.serviceWorker.register('sw.js');
render();