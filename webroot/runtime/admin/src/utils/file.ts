/**
 * 下载文件
 * @param file
 * @param fileName
 */
export function downloadFile (file: any, fileName: string) {
    const blob = new Blob([file], { type: 'application/octet-stream;charset=UTF-8' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.style.display = 'none'
    link.href = url
    link.setAttribute('download', fileName)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
}